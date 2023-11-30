package ru.quipy.task.eda.logic

import ru.quipy.task.eda.api.TaskAggregate
import ru.quipy.task.eda.logic.TaskAggregateState

import ru.quipy.task.eda.api.TaskCreatedEvent
import ru.quipy.task.eda.api.TaskUpdatedEvent
import ru.quipy.task.eda.api.TaskAssignedEvent
import java.util.*

fun TaskAggregateState.create(
    id: UUID, taskName: String
): TaskCreatedEvent {
    return TaskCreatedEvent(id, taskName)
}

fun TaskAggregateState.update(
    id: UUID, newTaskName: String
): TaskUpdatedEvent {
    return TaskUpdatedEvent(id, newTaskName)
}

fun TaskAggregateState.assignTask(
    userId: UUID, taskId: UUID
): TaskAssignedEvent {
    return TaskAssignedEvent(userId, taskId)
}
