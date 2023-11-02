package ru.quipy.logic

import ru.quipy.api.TagAssignedToTaskEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskExecutorChangedEvent
import ru.quipy.api.TaskNameChangedEvent
import ru.quipy.api.TaskStatusChangedEvent
import java.util.*

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */

// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

// Create Project
fun TaskAggregateState.create(id: UUID, title: String, projectId: UUID, tagId: UUID, creatorId: UUID): TaskCreatedEvent {
    return TaskCreatedEvent(
            taskId = id,
            projectId = projectId,
            taskName = title,
            creatorId = creatorId,
            tagId = tagId,
            executors = emptyList()
    )
}

fun TaskAggregateState.changeTitle(id: UUID, newTitle: String, projectId: UUID): TaskNameChangedEvent {
    return TaskNameChangedEvent(
            taskId = id,
            projectId = projectId,
            newTaskName = newTitle,
    )
}

fun TaskAggregateState.changeStatus(id: UUID, statusId: UUID, projectId: UUID): TaskStatusChangedEvent {
    return TaskStatusChangedEvent(
            taskId = id,
            projectId = projectId,
            statusId = statusId,
    )
}


fun TaskAggregateState.assignUserToTask(id: UUID, userId: UUID, projectId: UUID): TaskExecutorChangedEvent {
    return TaskExecutorChangedEvent(
            taskId = id,
            projectId = projectId,
            userId = userId,
    )
}

fun TaskAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}