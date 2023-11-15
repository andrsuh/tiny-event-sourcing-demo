package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TagAssignedToTaskEvent
import ru.quipy.api.TagChangeColorEvent
import ru.quipy.api.TagChangeNameEvent
import ru.quipy.api.TagCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskExecutorChangedEvent
import ru.quipy.api.TaskNameChangedEvent
import ru.quipy.api.UserAssignedToProjectEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.ProjectMemberEntity
import ru.quipy.logic.Task
import ru.quipy.logic.assignTagToTask
import ru.quipy.logic.assignUserToProject
import ru.quipy.logic.assignUserToTask
import ru.quipy.logic.changeColor
import ru.quipy.logic.changeName
import ru.quipy.logic.changeTaskTitle
import ru.quipy.logic.create
import ru.quipy.logic.createTag
import ru.quipy.logic.createTask
import java.util.*
import javax.annotation.meta.TypeQualifierNickname

@RestController
@RequestMapping("/projects")
class ProjectController(
        val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: UUID): ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/add-user")
    fun addUserToProject(@PathVariable projectId: UUID, @RequestParam userId: UUID, @RequestParam username: String, @RequestParam nickname: String): UserAssignedToProjectEvent {
        return projectEsService.update(projectId) {
            it.assignUserToProject(userId, username, nickname)
        }
    }

    @GetMapping("/{projectId}/get-project-members")
    fun getProjectMembers(@PathVariable projectId: UUID): List<ProjectMemberEntity> {
        return projectEsService.getState(projectId)?.projectMembers?.map { it.value } ?: emptyList()
    }

    @PostMapping("/{projectId}/create-tag")
    fun createTag(@PathVariable projectId: UUID, @RequestParam name: String, @RequestParam color: String): TagCreatedEvent {
        return projectEsService.update(projectId) { it.createTag(name, color) }
    }

    @PostMapping("/{projectId}/change-tag-name/{tagId}")
    fun changeTagName(@PathVariable projectId: UUID, @PathVariable tagId: UUID, @RequestParam name: String): TagChangeNameEvent {
        return projectEsService.update(projectId) { it.changeName(name, tagId) }
    }

    @PostMapping("/{projectId}/change-tag-color/{tagId}")
    fun changeTagColor(@PathVariable projectId: UUID, @PathVariable tagId: UUID, @RequestParam color: String): TagChangeColorEvent {
        return projectEsService.update(projectId) { it.changeColor(color, tagId) }
    }

    @PostMapping("/{projectId}/{taskTitle}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskTitle: String, @RequestParam creatorId: UUID, @RequestParam tagId: UUID): TaskCreatedEvent {
        return projectEsService.update(projectId) { it.createTask(UUID.randomUUID(), taskTitle, projectId, tagId, creatorId) }
    }

    @PostMapping("/{projectId}/{taskId}/change-name")
    fun changeTaskName(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam newTitle: String): TaskNameChangedEvent {
        return projectEsService.update(projectId) { it.changeTaskTitle(taskId, newTitle, projectId) }
    }

    @GetMapping("/{projectId}/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): Task? {
        return projectEsService.getState(projectId)?.tasks?.get(taskId)
    }

    @GetMapping("/{projectId}/tasks")
    fun getProjectTasks(@PathVariable projectId: UUID): MutableMap<UUID, Task>? {
        return projectEsService.getState(projectId)?.tasks
    }

    @PutMapping("/{projectId}/{taskId}/set-status/{statusId}")
    fun setTaskStatus(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable statusId: UUID): TagAssignedToTaskEvent {
        return projectEsService.update(projectId) { it.assignTagToTask(taskId, statusId) }
    }

    @PutMapping("/{projectId}/{taskId}/set-executor")
    fun setTaskExecutor(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam userId: UUID): TaskExecutorChangedEvent {
        return projectEsService.update(projectId) { it.assignUserToTask(taskId, userId, projectId) }
    }
}