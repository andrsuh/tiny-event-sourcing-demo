package ru.quipy.controllers.projectManagment

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.commands.projectManagment.project.addStatus
import ru.quipy.commands.projectManagment.project.removeStatus
import ru.quipy.core.EventSourcingService
import ru.quipy.dtos.projectManagment.status.CreateStatusDto
import ru.quipy.dtos.projectManagment.status.StatusInfoDto
import ru.quipy.dtos.projectManagment.status.getStatusInfoDto
import ru.quipy.events.projectManagment.project.StatusAddedEvent
import ru.quipy.events.projectManagment.project.StatusRemovedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

@RestController("/statuses")
class StatusController(
    val projectEventSourcingService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @PostMapping("/{projectId}")
    fun addStatus(@PathVariable projectId: UUID, @RequestBody createDto: CreateStatusDto): StatusAddedEvent {
        return projectEventSourcingService.update(projectId) {
            it.addStatus(
                UUID.randomUUID(),
                createDto.name,
                createDto.color
            )
        }
    }

    @GetMapping("/{projectId}/{statusId}")
    fun getStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): StatusInfoDto? {
        return projectEventSourcingService
            .getState(projectId)
            ?.getStatusInfoDto(statusId)
    }

    @DeleteMapping("/{projectId}/{statusId}")
    fun removeStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID): StatusRemovedEvent {
        return projectEventSourcingService.update(projectId) {
            it.removeStatus(
                statusId,
            )
        }
    }
}