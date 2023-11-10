package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
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
@RequestMapping("/tasks")
class TaskController (
        val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {
    @PostMapping("/{taskTitle}/{projectId}")
    fun createTask(@PathVariable taskTitle: String,@PathVariable projectId: UUID,  @RequestParam creatorId: String) : TaskCreatedEvent{
        return taskEsService.create { it.create(UUID.randomUUID(), taskTitle, creatorId, projectId)  }
    }

    @GetMapping("/get/{taskId}")
    fun getTask(@PathVariable taskId: UUID) : TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

    @PostMapping("/change/{taskId}/{taskTitle}")
    fun changeTaskTitle(@PathVariable taskId: UUID, @PathVariable taskTitle: String) : TaskTitleChangedEvent {
        return taskEsService.update(taskId) {it.changeTitle(taskId ,taskTitle)}
    }

    @PostMapping("executors/{taskId}/{userId}")
    fun addExecutor(@PathVariable taskId: UUID, @PathVariable userId: UUID) : TaskAddedExecutorEvent {
        return taskEsService.update(taskId) {it.addExecutor(userId)}
    }

    @PostMapping("statuses/{taskId}/{statusId}")
    fun assignStatus(@PathVariable taskId: UUID, @PathVariable statusId: UUID) : StatusAssignedToTaskEvent {
        return taskEsService.update(taskId) {it.assignStatus(taskId, statusId)}
    }

    @DeleteMapping("delete/{taskId}/{userId}")
    fun deleteExecutor(@PathVariable taskId: UUID, @PathVariable userId: UUID) : TaskRemovedExecutor {
        return taskEsService.update(taskId) {it.removeExecutor(userId)}
    }

}