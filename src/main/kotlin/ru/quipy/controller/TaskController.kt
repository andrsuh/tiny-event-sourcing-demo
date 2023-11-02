package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskExecutorChangedEvent
import ru.quipy.api.TaskNameChangedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.assignUserToTask
import ru.quipy.logic.changeStatus
import ru.quipy.logic.changeTitle
import ru.quipy.logic.create
import java.util.*

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
@RestController
@RequestMapping("{projectId}/tasks")
class TaskController(
        val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
        val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @PostMapping("/{taskTitle}")
    fun createTask(@PathVariable projectId: UUID, @PathVariable taskTitle: String, @RequestParam creatorId: UUID, @RequestParam tagId: UUID): TaskCreatedEvent {
        return taskEsService.create { it.create(UUID.randomUUID(), taskTitle, projectId, tagId, creatorId) }
    }

    @PostMapping("/{taskId}/change-name")
    fun changeTaskName(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam newTitle: String): TaskNameChangedEvent {
        return taskEsService.update(taskId) { it.changeTitle(taskId, newTitle, projectId) }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

    @GetMapping()
    fun getProjectTasks(@PathVariable projectId: UUID): List<UUID> {
        return projectEsService.getState(projectId)?.tasks?: emptyList()
    }

    @PutMapping("/{taskId}/set-status")
    fun setTaskStatus(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam statusId: UUID): TaskStatusChangedEvent {
        return taskEsService.update(taskId) { it.changeStatus(taskId, statusId, projectId) }
    }

    @PutMapping("/{taskId}/set-executor")
    fun setTaskExecutor(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam userId: UUID): TaskExecutorChangedEvent {
        return taskEsService.update(taskId) { it.assignUserToTask(taskId, userId, projectId) }
    }
}