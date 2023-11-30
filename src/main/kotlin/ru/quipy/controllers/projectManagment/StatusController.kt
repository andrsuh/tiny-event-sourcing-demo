package ru.quipy.controllers.projectManagment

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.commands.projectManagment.project.addStatus
import ru.quipy.commands.projectManagment.project.removeStatus
import ru.quipy.core.EventSourcingService
import ru.quipy.dtos.status.CreateStatusDto
import ru.quipy.dtos.status.StatusDto
import ru.quipy.dtos.status.StatusInfoDto
import ru.quipy.events.projectManagment.project.StatusAddedEvent
import ru.quipy.events.projectManagment.project.StatusRemovedEvent
import ru.quipy.services.projectManaging.StatusQueryHandlingService
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

@RestController
@RequestMapping("/projects")
class StatusController(
    val projectEventSourcingService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val statusQueryHandlingService: StatusQueryHandlingService,
) {
    @PostMapping("/{projectId}/statuses/")
    fun addStatus(@PathVariable projectId: UUID, @RequestBody createDto: CreateStatusDto): StatusAddedEvent {
        return projectEventSourcingService.update(projectId) {
            it.addStatus(
                UUID.randomUUID(),
                createDto.name,
                createDto.color
            )
        }
    }

    @GetMapping("/{projectId}/statuses/{statusId}")
    fun getStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): StatusDto? {
        return statusQueryHandlingService
            .findStatusById(projectId, statusId)
    }

    @GetMapping("/{projectId}/statuses/")
    fun getStatus(@PathVariable projectId: UUID): List<StatusInfoDto>? {
        return statusQueryHandlingService
            .findStatusesByProjectId(projectId)
    }

    @DeleteMapping("/{projectId}/statuses/{statusId}")
    fun removeStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): StatusRemovedEvent {
        return projectEventSourcingService.update(projectId) {
            it.removeStatus(
                statusId,
            )
        }
    }
}