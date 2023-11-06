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
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String): ProjectCreatedEvent {
        return projectEsService.create { it.createProject(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getAccount(@PathVariable projectId: UUID): ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/createStatus/{statusName}")
    fun createStatus(
        @PathVariable statusName: String,
        @RequestParam projectId: String,
        @RequestParam color: String
    ): StatusCreatedEvent {
        return projectEsService.update(UUID.fromString(projectId)) {
            it.createStatus(statusName, color)
        }
    }

    @PutMapping("/{name}")
    fun changeProjectName(@PathVariable name: String, @RequestParam projectId: String): ProjectNameChangedEvent {
        return projectEsService.update(UUID.fromString(projectId)) {
            it.changeProjectName(name)
        }
    }

    @DeleteMapping("/{statusId}")
    fun deleteStatus(@PathVariable statusId: String, @RequestParam projectId: String): StatusDeletedEvent {
        return projectEsService.update(UUID.fromString(projectId)) {
            it.deleteStatus(UUID.fromString(statusId))
        }
    }
}