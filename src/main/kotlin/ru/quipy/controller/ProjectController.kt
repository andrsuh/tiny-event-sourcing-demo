package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String): ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/status/{statusName}/{statusColor}")
    fun createStatus(
        @PathVariable projectId: UUID, @PathVariable statusName: String,
        @PathVariable statusColor: String
    ): StatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.addStatus(statusName, statusColor)
        }
    }

    @DeleteMapping("/{projectId}/status/{statusId}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): StatusDeletedEvent {
        return projectEsService.update(projectId) {
            it.deleteStatus(projectId, statusId)
        }
    }

    @PutMapping("/{projectId}/status/assign/{taskId}/{statusId}")
    fun assignStatus(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @PathVariable statusId: UUID
    ): StatusAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignStatus(projectId, taskId, statusId)
        }
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String): TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskName)
        }
    }

    @PutMapping("/{projectId}/task/{taskId}/{newName}")
    fun renameTask(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @PathVariable newName: String
    ): TaskRenamedEvent {
        return projectEsService.update(projectId) {
            it.renameTask(projectId, taskId, newName)
        }
    }

    @PutMapping("{projectId}/task/{taskId}/assign/{userId}")
    fun assignUser(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @PathVariable userId: UUID
    ): UserAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignUser(projectId, taskId, userId)
        }
    }
}
