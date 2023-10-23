package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.aggregate.ProjectAggregate
import ru.quipy.api.event.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.commands.*
import ru.quipy.logic.state.ProjectAggregateState
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: UUID) : ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/tasks/{taskName}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String) : ProjectTaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(taskName)
        }
    }
    @PostMapping("/{projectId}/tasks/status")
    fun changeTaskStatus(@PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam statusId: UUID) : ProjectTaskStatusChangedEvent {
        return projectEsService.update(projectId) {
            it.projectTaskStatusChange(statusId, taskId)
        }
    }

    @PostMapping("/{projectId}/tasks/assign")
    fun assignMemberToTask(@PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam memberId: UUID) : ProjectTaskMemberAssignedEvent {
        return projectEsService.update(projectId) {
            it.projectTaskMemberAssign(memberId, taskId)
        }
    }

    @PostMapping("/{projectId}/tasks/title")
    fun changeTaskTitle(@PathVariable projectId: UUID, @RequestParam taskId: UUID, @RequestParam title: String) : ProjectTaskTitleChangedEvent {
        return projectEsService.update(projectId) {
            it.projectTaskTitleChange(title, taskId)
        }
    }

    @PostMapping("/{projectId}/members")
    fun addMember(@PathVariable projectId: UUID, @RequestParam memberId: UUID) : ProjectMemberAddedEvent {
        return projectEsService.update(projectId) {
            it.projectMemberAdd(memberId)
        }
    }

    @PostMapping("/{projectId}/title")
    fun changeTitle(@PathVariable projectId: UUID, @RequestParam title: String) : ProjectTitleChangedEvent {
        return projectEsService.update(projectId) {
            it.projectTitleChange(title);
        }
    }

    @DeleteMapping("/{projectId}/status")
    fun deleteStatus(@PathVariable projectId: UUID, @RequestParam statusId: UUID) : ProjectStatusDeletedEvent {
        return projectEsService.update(projectId) {
            it.projectStatusDelete(statusId)
        }
    }

    @PostMapping("/{projectId}/status")
    fun addStatus(@PathVariable projectId: UUID, @RequestParam statusTitle: String, @RequestParam color: String) : ProjectStatusAddedEvent {
        return projectEsService.update(projectId) {
            it.projectStatusCreate(statusTitle, color)
        }
    }
}