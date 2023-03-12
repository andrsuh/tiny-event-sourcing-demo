package ru.quipy.logic

import ru.quipy.api.*
import java.util.*

fun TaskAggregateState.create(name: String): TaskCreatedEvent {
    return TaskCreatedEvent(taskId = UUID.randomUUID(), taskName = name)
}

fun TaskAggregateState.setStatus(status: StatusEntity): StatusSetEvent {
    return StatusSetEvent(taskId = this.getId(), status = status)
}

fun TaskAggregateState.addExecutor(name: String): ExecutorAddedEvent {
    return ExecutorAddedEvent(taskId = this.getId(), executorName = name)
}

fun TaskAggregateState.deleteExecutor(name: String): ExecutorDeletedEvent {
    return ExecutorDeletedEvent(taskId = this.getId(), executorName = name)
}

fun TaskAggregateState.renameTask(name: String): TaskRenamedEvent {
    return TaskRenamedEvent(taskId = this.getId(), taskName = name)
}
