package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String) : ProjectCreatedEvent {
        return projectEsService.create { it.create(projectTitle, creatorId) }
    }

    @PostMapping("/addParticipant/{projectId}")
    fun addParticipant(@PathVariable projectId: UUID, @RequestParam participantId: UUID) : ParticipantAddedEvent {
        return projectEsService.update(projectId) { it.addParticipant(participantId) }
    }

    @PostMapping("/updateTitle/{projectId}")
    fun updateTitle(@PathVariable projectId: UUID, @RequestParam title: String) : ProjectTitleUpdated {
        return projectEsService.update(projectId) { it.updateTitle(title) }
    }

    @PostMapping("/createStatus/{projectId}")
    fun createStatus(@PathVariable projectId: UUID, @RequestParam title: String, @RequestParam color: String, @RequestParam numberOfTaskInStatus: Int) : StatusCreatedEvent {
        return projectEsService.update(projectId) { it.createStatus(title, color, numberOfTaskInStatus) }
    }

    @PostMapping("/deleteStatus/{projectId}")
    fun deleteStatus(@PathVariable projectId: UUID, @RequestParam statusId: UUID) : StatusDeletedEvent {
        return projectEsService.update(projectId) { it.deleteStatus(statusId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @GetMapping("/statuses/{projectId}")
    fun getProjectStatuses(@PathVariable projectId: UUID) : Collection<StatusEntity> {
        return projectEsService.getState(projectId)?.projectStatuses?.values ?: listOf()
    }

}
