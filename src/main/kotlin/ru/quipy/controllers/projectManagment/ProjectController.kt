package ru.quipy.controllers.projectManagment

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.commands.projectManagment.project.addParticipant
import ru.quipy.commands.projectManagment.project.create
import ru.quipy.core.EventSourcingService
import ru.quipy.dtos.project.CreateProjectDto
import ru.quipy.dtos.project.ProjectDto
import ru.quipy.dtos.project.ProjectInfoDto
import ru.quipy.dtos.user.ParticipantDto
import ru.quipy.events.projectManagment.project.ParticipantAddedEvent
import ru.quipy.events.projectManagment.project.ProjectCreatedEvent
import ru.quipy.services.projectManaging.ProjectQueryHandlingService
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEventSourcingService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val projectQueryHandlingService: ProjectQueryHandlingService,
) {

    @PostMapping
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
    fun getProject(@PathVariable projectId: UUID): ProjectDto? {
        return projectQueryHandlingService
            .findProjectById(projectId)
    }

    @GetMapping("/")
    fun getProject(@RequestParam(required = false) creatorId: UUID?,
                   @RequestParam(required = false) participantId: UUID?): List<ProjectInfoDto> {
        return projectQueryHandlingService
            .findProjectsByFilters(creatorId, participantId)
    }

    @PostMapping("/{projectId}/participants/")
    fun assigneeToTask(
        @PathVariable projectId: UUID,
        @RequestBody participantDto: ParticipantDto
    ): ParticipantAddedEvent {
        return projectEventSourcingService.update(projectId) {
            it.addParticipant(
                participantDto.id,
            )
        }
    }
}