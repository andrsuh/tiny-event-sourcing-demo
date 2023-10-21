package ru.quipy.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.TaskAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.TaskAggregateState
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController (
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {

}