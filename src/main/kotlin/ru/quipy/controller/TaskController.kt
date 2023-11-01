package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*
/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
@RestController
@RequestMapping("{projectId}/tasks")
class TaskController (
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
){
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
    fun getProjectTasks(@PathVariable projectId: UUID): List<TaskEntity> {
        return projectEsService.getState(projectId)?.tasks?.map { it.value } ?: emptyList()
    }

    @PutMapping("/{taskId}/set-status")
    fun setTaskStatus(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam newStatus: String) : TaskStatusChangedEvent {
        return taskEsService.update(taskId) {it.changeStatus(taskId, newStatus, projectId)}
    }

    @PutMapping("/{taskId}/set-executor")
    fun setTaskExecutor(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam newStatus: String) : TaskExecutorChangedEvent {
        return projectEsService.update(projectId) {it.assignUserToTask(taskId, newStatus, projectId)}
    }
}