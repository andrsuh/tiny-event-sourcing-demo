package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.create
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
        val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {

    @PostMapping("/{taskName}")
    fun createTask(@PathVariable taskName: String) : TaskCreatedEvent {
        return taskEsService.create { it.create(UUID.randomUUID(), taskName) }
    }
}