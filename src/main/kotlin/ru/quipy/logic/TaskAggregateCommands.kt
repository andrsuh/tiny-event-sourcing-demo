package ru.quipy.logic

import ru.quipy.api.StatusSetEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskExecutorAddedEvent
import ru.quipy.api.TaskExecutorRemovedEvent
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions
fun TaskAggregateState.createTask(taskTitle: String, description: String, projectId: UUID, statusId: UUID): TaskCreatedEvent {
    return TaskCreatedEvent(
        taskId = UUID.randomUUID(),
        taskTitle = taskTitle,
        description = description,
        projectId = projectId,
        statusId = statusId
    )
}

fun TaskAggregateState.setStatus(taskId: UUID, statusId: UUID): StatusSetEvent {
    return StatusSetEvent(
        taskId = taskId,
        statusId = statusId
    )
}

fun TaskAggregateState.addExecutor(taskId: UUID, userId: UUID): TaskExecutorAddedEvent {
    if (executorsId.contains(userId))
        throw Exception()
    return TaskExecutorAddedEvent(
        taskId = taskId,
        userId = userId
    )
}

fun TaskAggregateState.removeExecutor(taskId: UUID, userId: UUID): TaskExecutorRemovedEvent {
    if (!executorsId.contains(userId))
        throw Exception()
    return TaskExecutorRemovedEvent(
        taskId = taskId,
        userId = userId
    )
}
