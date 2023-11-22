package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.projections.UserProjects
import ru.quipy.projections.UserProjectsCacheRepository
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val repo: UserProjectsCacheRepository
) {

    @PostMapping()
    fun createProject(@RequestParam projectTitle: String, @RequestParam creatorId: UUID) : ProjectAggregateState? {
        val project = projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }

        projectEsService.update(project.projectId) {
            it.createTag(UUID.randomUUID(), "CREATED", "blue")
        }
        return projectEsService.getState(project.projectId)
    }

    @GetMapping("/{projectId}")
    @RequestMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        val result = projectEsService.getState(projectId)
        println("Returning result for projectId $projectId: $result")
        return result
    }

    @GetMapping("user/{userId}")
    @RequestMapping("user/{userId}")
    fun getUserProject(@PathVariable userId: UUID) : UserProjects {
        return repo.findById(userId).get()
    }

    @PostMapping("/{projectId}/tags")
    fun createTag(@PathVariable projectId: UUID, @RequestParam tagName: String, @RequestParam tagColor: String) : TagCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTag(UUID.randomUUID(), tagName, tagColor)
        }
    }

    @DeleteMapping("/{projectId}/tags/{tagId}")
    fun deleteTag(@PathVariable projectId: UUID, @PathVariable tagId: UUID) : TagDeletedEvent {
        return projectEsService.update(projectId) {
            it.deleteTag(tagId)
        }
    }
    @PostMapping("/{projectId}/tasks")
    fun createTask(@PathVariable projectId: UUID, @RequestParam taskName: String) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(UUID.randomUUID(), taskName)
        }
    }

    @PutMapping("/{projectId}/tasks/{taskId}")
    fun updateTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam taskName: String) : TaskRenamedEvent {
        return projectEsService.update(projectId) {
            it.renameTask(taskId, taskName)
        }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/assign/tag/{tagId}")
    fun assignTag(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable tagId: UUID ) : TagAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignTagToTask(tagId, taskId)
        }
    }

    @PostMapping("/{projectId}/user/{userId}")
    fun addUser(@PathVariable projectId: UUID, @PathVariable userId: UUID) : UserAddedEvent {
        return projectEsService.update(projectId) {
            it.addUser(userId)
        }
    }

    @PostMapping("/{projectId}/tasks/{taskId}/assign/user/{userId}")
    fun assignUser(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable userId: UUID ) : UserAssignedToTaskEvent {
        return projectEsService.update(projectId) {
            it.assignUserToTask(taskId, userId)
        }
    }
}