package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.TaskStatusEntity
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.addProjectMember
import ru.quipy.logic.assignExecutor
import ru.quipy.logic.assignTaskStatus
import ru.quipy.logic.changeTitle
import ru.quipy.logic.create
import ru.quipy.logic.createTaskStatus
import ru.quipy.logic.removeTaskStatus
import ru.quipy.logic.retractExecutor
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    private val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    private val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    private val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: UUID): ProjectCreatedEvent {
        userEsService.getState(creatorId)
            ?: throw NotFoundException("No such user: $creatorId")

        return projectEsService.create { it.create(title = projectTitle, creatorId = creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ProjectAggregateState? =
        projectEsService.getState(projectId)
            ?: throw NotFoundException("No such project: $projectId")

    @PatchMapping("/{projectId}")
    fun changeTitle(@PathVariable projectId: UUID, @RequestParam title: String): ProjectTitleChangedEvent =
        projectEsService.update(projectId) {
            it.changeTitle(projectId, title)
        }

    @PostMapping("/{projectId}/members")
    fun addProjectMember(@PathVariable projectId: UUID, @RequestParam memberId: UUID): ProjectMemberAddedEvent {
        userEsService.getState(memberId )
            ?: throw NotFoundException("No such user: $memberId")

        return projectEsService.update(projectId) {
            it.addProjectMember(projectId = projectId, memberId = memberId)
        }
    }

    @PostMapping("/{projectId}/taskStatuses")
    fun addTaskStatus(@PathVariable projectId: UUID, @RequestParam name: String): TaskStatusCreatedEvent =
        projectEsService.update(projectId) {
            it.createTaskStatus(name)
        }

    @DeleteMapping("/{projectId}/taskStatuses/{taskStatusId}")
    fun removeTaskStatus(@PathVariable projectId: UUID, @PathVariable taskStatusId: UUID): TaskStatusRemovedEvent =
        projectEsService.update(projectId) {
            it.removeTaskStatus(taskStatusId)
        }

    @PostMapping("/{projectId}/tasks/")
    fun createTask(
        @PathVariable projectId: UUID,
        @RequestParam taskName: String,
        @RequestParam creatorId: UUID,
    ): TaskCreatedEvent {
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

    @GetMapping("/{projectId}/tasks/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskAggregateState? {
        projectEsService.getState(projectId)
            ?: throw NotFoundException("No such project: $projectId")

        return taskEsService.getState(taskId)
    }

    @PatchMapping("/{projectId}/tasks/{taskId}")
    fun assignTaskStatus(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam taskStatusId: UUID,
    ): TaskStatusAssignedToTaskEvent {
        val projectState = projectEsService.getState(projectId)
            ?: throw NotFoundException("No such project: $projectId")

        projectState.taskStatuses[taskStatusId]
            ?: throw NotFoundException("No such task status: $taskStatusId")

        return taskEsService.update(taskId) {
            it.assignTaskStatus(projectId, taskId, taskStatusId)
        }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/executors")
    fun assignExecutor(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam executorId: UUID,
    ): ExecutorAssignedToTaskEvent {
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

    @DeleteMapping("/{projectId}/tasks/{taskId}/executors")
    fun retractExecutor(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam executorId: UUID,
    ): ExecutorRetractedFromTaskEvent {
        projectEsService.getState(projectId)
            ?: throw NotFoundException("No such project: $projectId")

        return taskEsService.update(taskId) {
            it.retractExecutor(projectId = projectId, taskId = taskId, executorId = executorId)
        }
    }

}