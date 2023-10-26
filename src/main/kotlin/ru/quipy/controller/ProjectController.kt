package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam createdBy: UUID) : ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, createdBy) }
    }

    @GetMapping("/{projectId}")
    fun getAccount(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTaskInProject(taskName)
        }
    }
    @PostMapping("/{projectId}/statuses")
    fun createStatus(
        @PathVariable projectId: UUID,
        @RequestParam color: StatusColor,
        @RequestParam status: Status
    ): StatusCreatedEvent {
        return projectEsService.update(projectId) { it.createStatusInProject(projectId, color, status) }
    }

    @PostMapping("/{projectId}/tags")
    fun createTag(@PathVariable projectId: UUID, @RequestParam tagName: String): TagCreatedEvent {
        return projectEsService.update(projectId) { it.createTag(tagName) }
    }

    @PostMapping("/{projectId}/participants")
    fun addParticipant(@PathVariable projectId: UUID, @RequestParam userId: UUID): ParticipantAddedToProjectEvent {
        return projectEsService.update(projectId) { it.addParticipantToProject(projectId, userId) }
    }

    @PutMapping("/{projectId}/tasks/{taskId}")
    fun renameTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam newName: String): TaskRenamedEvent {
        return projectEsService.update(projectId) { it.renameTask(taskId, newName) }
    }

    @PostMapping("/tasks/{taskId}/assign")
    fun assignTaskToUser(@PathVariable taskId: UUID, @RequestParam userId: UUID): TaskAssignedToUserEvent {
        return projectEsService.update(taskId) { it.assignTaskToUser(taskId, userId) }
    }

    @PostMapping("/tasks/{taskId}/self-assign")
    fun selfAssignTask(@PathVariable taskId: UUID, @RequestParam userId: UUID): TaskSelfAssignedEvent {
        return projectEsService.update(taskId) { it.selfAssignTask(taskId, userId) }
    }

    @PostMapping("/tasks/{taskId}/status")
    fun assignStatusToTask(@PathVariable taskId: UUID, @RequestParam statusId: UUID): StatusAssignedToTaskEvent {
        return projectEsService.update(taskId) { it.assignStatusToTask(taskId, statusId) }
    }

    @PutMapping("/tasks/{taskId}/status")
    fun changeTaskStatus(@PathVariable taskId: UUID, @RequestParam newStatusId: UUID): TaskStatusChangedEvent {
        return projectEsService.update(taskId) { it.changeTaskStatus(taskId, newStatusId) }
    }

    @DeleteMapping("/{projectId}")
    fun deleteProject(@PathVariable projectId: UUID): ProjectDeletedEvent {
        return projectEsService.update(projectId) { it.deleteProject(projectId) }
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    fun deleteTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskDeletedEvent {
        return projectEsService.update(projectId) { it.deleteTask(taskId) }
    }

    @DeleteMapping("/{projectId}/statuses/{statusId}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): StatusDeletedEvent {
        return projectEsService.update(projectId) { it.deleteStatus(statusId) }
    }
}