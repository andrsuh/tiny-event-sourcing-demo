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
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String) : ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @PutMapping("/{projectId}/status")
    fun createStatus(@PathVariable projectId: UUID,
                     @RequestParam name: String,
                     @RequestParam color: String) : StatusCreatedEvent {
        return projectEsService.update(projectId) { it ->
            it.createStatus(name, color)
        }
    }

    @PutMapping("/{projectId}/status")
    fun deleteStatus(@PathVariable projectId: UUID,
                     @RequestParam name: String,
                     @RequestParam color: String) : StatusDeletedEvent {
        return projectEsService.update(projectId) { it.deleteStatus(name, color) }
    }

    @GetMapping("/{projectId}")
    fun getAccount(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

//    @PostMapping("/{projectId}/tasks/{taskName}")
//    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String) : TaskCreatedEvent {
//        return projectEsService.update(projectId) {
//            it.addTask(taskName)
//        }
//    }

    @PostMapping("/{taskName}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskName)
        }
    }
}