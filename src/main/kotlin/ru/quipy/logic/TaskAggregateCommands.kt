package ru.quipy.logic

import ru.quipy.api.*
import java.util.*

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */

// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

// Create Project
fun TaskAggregateState.create(id: UUID, title: String, projectId: UUID): TaskCreatedEvent {
    return TaskCreatedEvent(
        taskId = id,
        projectId = projectId,
        taskName = title,
    )
}

fun TaskAggregateState.changeTitle(id: UUID, newTitle: String, projectId: UUID): TaskNameChangedEvent {
    return TaskNameChangedEvent(
        taskId = id,
        projectId = projectId,
        newTaskName = newTitle,
    )
}

fun TaskAggregateState.changeStatus(id: UUID, newStatus: String, projectId: UUID): TaskStatusChangedEvent {
    return TaskStatusChangedEvent(
        taskId = id,
        projectId = projectId,
        newTaskStatus = newStatus,
    )
}
