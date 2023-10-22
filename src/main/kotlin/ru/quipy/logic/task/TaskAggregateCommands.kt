package ru.quipy.logic

import ru.quipy.api.*
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

fun TaskAggregateState.changeTitle(id: UUID, title: String): TaskTitleChangedEvent {
    return TaskTitleChangedEvent(
        taskId = id,
        title = title
    )
}

fun TaskAggregateState.assignStatus(id: UUID, statusId: UUID): StatusAssignedToTaskEvent {
    return StatusAssignedToTaskEvent(
        taskId = id,
        statusId = statusId,
    )
}

fun TaskAggregateState.addExecutor(id: UUID): TaskAddedExecutorEvent {
    if (executors.values.any { it.userId == id }) {
        throw IllegalArgumentException("User already exists: $id")
    }
    return TaskAddedExecutorEvent(taskId = this.getId(), executorId = id)
}

fun TaskAggregateState.removeExecutor(id: UUID): TaskRemovedExecutor {
    if (!executors.values.any { it.userId == id }) {
        throw IllegalArgumentException("User doesn't exists: $id")
    }

    return TaskRemovedExecutor(taskId = this.getId(), executorId = id)
}