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

    @PostMapping("")
    fun createProject(@RequestBody body: CreateProjectRequest) : ProjectCreatedEvent {
        return projectEsService.create { it.createProject(UUID.randomUUID(), body.projectName, body.creatorId, body.description) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/participants")
    fun addParticipant(@PathVariable projectId: UUID, @RequestBody body: AddParticipantRequest) : ParticipantAddedEvent {
        return projectEsService.update(projectId) { it.addParticipantById(userId = body.userId) }
    }
    @DeleteMapping("/{projectId}/participants/{userId}")
    fun deleteParticipant(@PathVariable projectId: UUID, @PathVariable userId: UUID) : LeaveProjectEvent{
        return projectEsService.update(projectId) { it.leaveProject(userId = userId) }
    }

    @PostMapping("/{projectId}/statuses")
    fun createStatus(@PathVariable projectId: UUID, @RequestBody body: CreateStatusRequest) : StatusCreatedEvent {
        return projectEsService.update(projectId) { it.createStatus(UUID.randomUUID(), body.statusName, body.colour) }
    }
    @DeleteMapping("/{projectId}/statuses/{statusId}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId:UUID) : StatusDeletedEvent{
        return projectEsService.update(projectId) { it.deleteStatus(statusId) }
    }
}

data class CreateProjectRequest (
    val projectName: String,
    val creatorId: UUID,
    val description: String
)

data class CreateStatusRequest (
    val statusName: String,
    val colour: String
)

data class AddParticipantRequest (
    val userId: UUID
)

