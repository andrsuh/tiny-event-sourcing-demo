package ru.quipy.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/projects/{projectId}/tasks")
class TaskController(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
) {
    @PostMapping("")
    fun create(
        @PathVariable projectId: UUID,
        @RequestParam name: String,
        @RequestParam creatorId: UUID,
    ): TaskCreatedEvent {
        ensureProjectExists(projectId)
        ensureUserIsProjectMember(creatorId, projectId)
        return taskEsService.create {
            it.createTask(
                taskId = UUID.randomUUID(),
                projectId = projectId,
                name = name,
                creatorId = creatorId,
                statusId = getDefaultStatus(projectId).id,
            )
        }
    }

    @GetMapping("/{taskId}")
    fun get(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskAggregateState {
        ensureProjectExists(projectId)
        val state = taskEsService.getState(taskId)
        if (state != null && state.projectId == projectId) {
            return state
        }
        throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @PostMapping("/{taskId}/assignees")
    fun assign(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam assigneeId: UUID,
    ): TaskAssignedEvent {
        ensureProjectExists(projectId)
        ensureTaskBelongsToProject(taskId, projectId)
        ensureUserIsProjectMember(assigneeId, projectId)
        return taskEsService.update(taskId) {
            it.assignTask(assigneeId)
        }
    }

    @PutMapping("/{taskId}/rename")
    fun rename(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam newName: String,
    ): TaskRenamedEvent {
        ensureProjectExists(projectId)
        ensureTaskBelongsToProject(taskId, projectId)
        return taskEsService.update(taskId) {
            it.renameTask(newName)
        }
    }

    @PutMapping("/{taskId}/setStatus")
    fun setStatus(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam statusId: UUID,
    ): TaskStatusSetEvent {
        ensureProjectExists(projectId)
        ensureTaskBelongsToProject(taskId, projectId)
        ensureStatusBelongsToProject(statusId, projectId)
        return taskEsService.update(taskId) {
            it.setTaskStatus(statusId)
        }
    }

    private fun ensureProjectExists(projectId: UUID) {
        if (projectEsService.getState(projectId) == null) {
            throw throw ResponseStatusException(HttpStatus.NOT_FOUND, "Project $projectId does not exist")
        }
    }

    private fun ensureTaskBelongsToProject(taskId: UUID, projectId: UUID) {
        val state = taskEsService.getState(taskId)
        if (state == null || state.projectId != projectId) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    private fun ensureStatusBelongsToProject(statusId: UUID, projectId: UUID) {
        val state = projectEsService.getState(projectId)
        if (state == null || !state.taskStatuses.containsKey(statusId)) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

    private fun ensureUserIsProjectMember(userId: UUID, projectId: UUID) {
        val state = projectEsService.getState(projectId)
        if (state == null || !state.members.contains(userId)) {
            throw ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "User $userId is not member of project $projectId"
            )
        }
    }

    private fun getDefaultStatus(projectId: UUID): TaskStatus {
        val project = projectEsService.getState(projectId)
        if (project == null) {
            throw IllegalStateException("Failed to find project $projectId")
        }
        return project.defaultStatus()
    }
}
