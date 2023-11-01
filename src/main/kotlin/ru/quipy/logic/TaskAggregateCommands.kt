package ru.quipy.logic

import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskNameChangedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.UserAddedToTaskEvent
import java.util.*


fun TaskAggregateState.createTask(projectId: UUID, name: String, description: String, creatorId: UUID): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = projectId, taskId = UUID.randomUUID(), taskName = name, description = description, creatorId = creatorId)
}

fun TaskAggregateState.assignStatusToTask(statusId: UUID, taskId: UUID): TaskStatusChangedEvent {
    return TaskStatusChangedEvent(statusId = statusId, taskId = taskId)
}

fun TaskAggregateState.assignUser(userId: UUID, taskId: UUID): UserAddedToTaskEvent {
    return UserAddedToTaskEvent(taskId = taskId, userId = userId)
}

fun TaskAggregateState.changeName(taskId: UUID, name: String): TaskNameChangedEvent {
    return TaskNameChangedEvent(taskId = taskId, taskName = name)
}


