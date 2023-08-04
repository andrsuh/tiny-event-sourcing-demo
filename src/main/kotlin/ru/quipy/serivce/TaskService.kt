package ru.quipy.serivce

import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@Service
class TaskService(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    val projectService: ProjectService,
) {
    fun create(projectId: UUID, name: String, creatorId: UUID): TaskCreatedEvent {
        ensureProjectExists(projectId)
        ensureUserIsProjectMember(creatorId, projectId)
        val defaultStatus = projectService.getProjectById(projectId).defaultStatus()
        return taskEsService.create {
            it.createTask(
                taskId = UUID.randomUUID(),
                projectId = projectId,
                name = name,
                creatorId = creatorId,
                statusId = defaultStatus.id,
            )
        }
    }

    fun getTaskById(projectId: UUID, taskId: UUID): TaskAggregateState {
        val state = taskEsService.getState(taskId)
        if (state != null && state.projectId == projectId) {
            return state
        }
        throw NoSuchEntity("Task $taskId is not found in project $projectId")
    }

    fun assignTask(projectId: UUID, taskId: UUID, assigneeId: UUID): TaskAssignedEvent {
        ensureProjectExists(projectId)
        ensureTaskBelongsToProject(taskId, projectId)
        ensureUserIsProjectMember(assigneeId, projectId)
        return taskEsService.update(taskId) {
            it.assignTask(assigneeId)
        }
    }

    fun setTaskStatus(projectId: UUID, taskId: UUID, statusId: UUID): TaskStatusSetEvent {
        ensureProjectExists(projectId)
        ensureTaskBelongsToProject(taskId, projectId)
        ensureStatusBelongsToProject(statusId, projectId)
        return taskEsService.update(taskId) {
            it.setTaskStatus(statusId)
        }
    }

    fun renameTask(projectId: UUID, taskId: UUID, newName: String): TaskRenamedEvent {
        ensureProjectExists(projectId)
        ensureTaskBelongsToProject(taskId, projectId)
        return taskEsService.update(taskId) {
            it.renameTask(newName)
        }
    }

    private fun ensureProjectExists(projectId: UUID) {
        if (!projectService.projectExists(projectId)) {
            throw throw NoSuchEntity("Project $projectId does not exist")
        }
    }

    private fun ensureUserIsProjectMember(userId: UUID, projectId: UUID) {
        check(projectService.isProjectMember(projectId, userId)) { "User $userId is not member of project $projectId" }
    }

    private fun ensureTaskBelongsToProject(taskId: UUID, projectId: UUID) {
        getTaskById(projectId, taskId)
    }

    private fun ensureStatusBelongsToProject(statusId: UUID, projectId: UUID) {
        check(projectService.projectHasTaskStatus(projectId, statusId)) {
            "Status $statusId does not belong to project $projectId"
        }
    }
}
