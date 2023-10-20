package ru.quipy.logic

import ru.quipy.api.StatusAssignedToTaskEvent
import ru.quipy.api.TaskAddedExecutorEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskTitleChangedEvent
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

fun TaskAggregateState.assignStatus(id: UUID, projectId: UUID, statusId: UUID): StatusAssignedToTaskEvent {
    return StatusAssignedToTaskEvent(
        taskId = id,
        projectId = projectId,
        statusId = statusId,
    )
}

fun TaskAggregateState.addExecutor(id: UUID): TaskAddedExecutorEvent {
    if (executors.values.any { it.userId == id }) {
        throw IllegalArgumentException("User already exists: $id")
    }
    return TaskAddedExecutorEvent(taskId = this.getId(), executorId = id)
}