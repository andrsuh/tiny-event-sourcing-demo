package ru.quipy.controllers.projectManagment

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.commands.projectManagment.project.create
import ru.quipy.core.EventSourcingService
import ru.quipy.dtos.projectManagment.project.CreateProjectDto
import ru.quipy.dtos.projectManagment.project.ProjectDto
import ru.quipy.dtos.projectManagment.project.toDto
import ru.quipy.events.projectManagment.project.ProjectCreatedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

@RestController("/projects")
class ProjectController(
    val projectEventSourcingService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping()
    fun createProject(@RequestBody createDto: CreateProjectDto): ProjectCreatedEvent {
        return projectEventSourcingService.create {
            it.create(
                UUID.randomUUID(),
                createDto.name,
                createDto.creatorId
            )
        }
    }

    @GetMapping("/{projectId}")
    fun getAccount(@PathVariable projectId: UUID): ProjectDto? {
        return projectEventSourcingService
            .getState(projectId)
            ?.toDto()
    }
}