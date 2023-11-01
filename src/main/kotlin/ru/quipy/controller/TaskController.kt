package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.assignStatusToTask
import ru.quipy.logic.createTask
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController (
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {

    @PostMapping("/{taskName}")
    fun createTask(@PathVariable taskName: String, @RequestParam projectId: String, @RequestParam description: String, @RequestParam creatorId: String) : TaskCreatedEvent {
        return taskEsService.create { it.createTask(UUID.fromString(projectId), taskName, description, UUID.fromString(creatorId)) }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID) : TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

    @PutMapping("/{taskId}")
    fun assignStatus(@PathVariable taskId: String, @RequestParam statusId: String) : TaskStatusChangedEvent {
        return taskEsService.update(UUID.fromString(taskId)){
            it.assignStatusToTask(UUID.fromString(statusId), UUID.fromString(taskId))
        }
    }
}