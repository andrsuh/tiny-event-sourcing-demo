package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.task.TaskAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.task.TaskAggregateState
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
    ) {

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID) : TaskAggregateState? {
        return taskEsService.getState(taskId)
    }
}