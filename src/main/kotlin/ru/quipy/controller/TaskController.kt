package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.TaskAssignedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskRenamedEvent
import ru.quipy.api.TaskStatusSetEvent
import ru.quipy.logic.TaskAggregateState
import ru.quipy.serivce.TaskService
import java.util.*

@RestController
@RequestMapping("/projects/{projectId}/tasks")
class TaskController(val taskService: TaskService) {
    @PostMapping("")
    fun create(
        @PathVariable projectId: UUID,
        @RequestParam name: String,
        @RequestParam creatorId: UUID,
    ): TaskCreatedEvent {
        return taskService.create(
            projectId = projectId,
            name = name,
            creatorId = creatorId,
        )
    }

    @GetMapping("/{taskId}")
    fun get(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskAggregateState {
        return taskService.getTaskById(projectId, taskId)
    }

    @PostMapping("/{taskId}/assignees")
    fun assign(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam assigneeId: UUID,
    ): TaskAssignedEvent {
        return taskService.assignTask(projectId, taskId, assigneeId)
    }

    @PutMapping("/{taskId}/rename")
    fun rename(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam newName: String,
    ): TaskRenamedEvent {
        return taskService.renameTask(projectId, taskId, newName)
    }

    @PutMapping("/{taskId}/setStatus")
    fun setStatus(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam statusId: UUID,
    ): TaskStatusSetEvent {
        return taskService.setTaskStatus(projectId, taskId, statusId)
    }
}
