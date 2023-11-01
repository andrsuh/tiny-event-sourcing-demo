package ru.quipy.logic

import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskDeletedEvent
import ru.quipy.api.TaskStatusUpdatedEvent
import ru.quipy.api.TaskTitleUpdatedEvent
import java.util.*


fun TaskAggregateState.create(
    id: UUID,
    projectId: UUID,
    creatorId: UUID,
    title: String,
    description: String
): TaskCreatedEvent {
    return TaskCreatedEvent(
        taskId = id,
        projectId = projectId,
        creatorId = creatorId,
        title = title,
        description = description,
        createdAt = System.currentTimeMillis()
    )
}

fun TaskAggregateState.updateTaskTitle(
    id: UUID,
    updaterId: UUID,
    title: String,
): TaskTitleUpdatedEvent {
    return TaskTitleUpdatedEvent(
        taskId = id,
        updaterId = updaterId,
        title = title,
        createdAt = System.currentTimeMillis()
    )
}

fun TaskAggregateState.updateTaskStatus(
    id: UUID,
    updaterId: UUID,
    status: TaskStatus,
): TaskStatusUpdatedEvent {
    return TaskStatusUpdatedEvent(
        taskId = id,
        updaterId = updaterId,
        status = status,
        createdAt = System.currentTimeMillis()
    )
}

fun TaskAggregateState.delete(
    id: UUID,
    userId: UUID,
): TaskDeletedEvent {
    return TaskDeletedEvent(
        taskId = id,
        userId = userId,
        createdAt = System.currentTimeMillis()
    )
}