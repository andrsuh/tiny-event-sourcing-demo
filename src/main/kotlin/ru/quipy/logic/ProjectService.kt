package ru.quipy.logic

import org.springframework.stereotype.Service
import ru.quipy.api.ExecutorAssignedToTaskEvent
import ru.quipy.api.ExecutorRetractedFromTaskEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectMemberAddedEvent
import ru.quipy.api.ProjectTitleChangedEvent
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAssignedToTaskEvent
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskStatusRemovedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.exception.NotFoundException
import java.util.*

@Service
class ProjectService(
    private val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    private val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    private val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
) {

    fun createProject(projectTitle: String, creatorId: UUID): ProjectCreatedEvent {
        userEsService.getState(creatorId)
            ?: throw NotFoundException("No such user: $creatorId")

        return projectEsService.create { it.create(title = projectTitle, creatorId = creatorId) }
    }

    fun getProject(projectId: UUID): ProjectAggregateState? =
        projectEsService.getState(projectId)
            ?: throw NotFoundException("No such project: $projectId")

    fun changeTitle(projectId: UUID, title: String): ProjectTitleChangedEvent =
        projectEsService.update(projectId) {
            it.changeTitle(projectId, title)
        }

    fun addProjectMember(projectId: UUID, memberId: UUID): ProjectMemberAddedEvent {
        userEsService.getState(memberId)
            ?: throw NotFoundException("No such user: $memberId")

        return projectEsService.update(projectId) {
            it.addProjectMember(projectId = projectId, memberId = memberId)
        }
    }

    fun addTaskStatus(projectId: UUID, name: String): TaskStatusCreatedEvent =
        projectEsService.update(projectId) {
            it.createTaskStatus(name)
        }

    fun removeTaskStatus(projectId: UUID, taskStatusId: UUID): TaskStatusRemovedEvent {
        // TODO check that no task in this project has this status

        return projectEsService.update(projectId) {
            it.removeTaskStatus(taskStatusId)
        }
    }

    fun createTask(projectId: UUID, taskName: String, creatorId: UUID): TaskCreatedEvent {
        val projectState = projectEsService.getState(projectId)
            ?: throw NotFoundException("No such project: $projectId")

        userEsService.getState(creatorId)
            ?: throw NotFoundException("No such user: $creatorId")

        val taskStatusId = projectState.taskStatuses.values
            .first { it.name == TaskStatusEntity.DEFAULT_TASK_STATUS_NAME }
            .id

        return taskEsService.create {
            it.create(projectId = projectId, taskName = taskName, creatorId = creatorId, defaultTaskStatusId = taskStatusId)
        }
    }

    fun getTask(projectId: UUID, taskId: UUID): TaskAggregateState? {
        projectEsService.getState(projectId)
            ?: throw NotFoundException("No such project: $projectId")

        return taskEsService.getState(taskId)
    }

    fun assignTaskStatus(projectId: UUID, taskId: UUID, taskStatusId: UUID): TaskStatusAssignedToTaskEvent {
        val projectState = projectEsService.getState(projectId)
            ?: throw NotFoundException("No such project: $projectId")

        projectState.taskStatuses[taskStatusId]
            ?: throw NotFoundException("No such task status: $taskStatusId")

        return taskEsService.update(taskId) {
            it.assignTaskStatus(projectId, taskId, taskStatusId)
        }
    }

    fun assignExecutor(projectId: UUID, taskId: UUID, executorId: UUID): ExecutorAssignedToTaskEvent {
        val projectState = projectEsService.getState(projectId)
            ?: throw NotFoundException("No such project: $projectId")

        userEsService.getState(executorId)
            ?: throw NotFoundException("No such user: $executorId")

        if (executorId !in projectState.memberIds) {
            throw IllegalStateException("User $executorId is not member of project $projectId")
        }

        return taskEsService.update(taskId) {
            it.assignExecutor(projectId = projectId, taskId = taskId, executorId = executorId)
        }
    }

    fun retractExecutor(projectId: UUID, taskId: UUID, executorId: UUID): ExecutorRetractedFromTaskEvent {
        projectEsService.getState(projectId)
            ?: throw NotFoundException("No such project: $projectId")

        return taskEsService.update(taskId) {
            it.retractExecutor(projectId = projectId, taskId = taskId, executorId = executorId)
        }
    }
}