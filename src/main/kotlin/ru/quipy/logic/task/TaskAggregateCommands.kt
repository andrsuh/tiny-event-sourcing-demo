package ru.quipy.logic

import ru.quipy.api.TaskAddedExecutorEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.project.*
import java.util.*


fun TaskAggregateState.create(id: UUID, title: String, creatorId: String, projectId: UUID): TaskCreatedEvent {
    return TaskCreatedEvent(
            taskId = id,
            title = title,
            creatorId = creatorId,
            projectId = projectId
            )
}

fun TaskAggregateState.addExecutor(id: UUID): TaskAddedExecutorEvent {
    if (executors.values.any { it.userId == id }) {
        throw IllegalArgumentException("User already exists: $id")
    }
    return TaskAddedExecutorEvent(taskId = this.getId(), executorId = id)
}