package ru.quipy.logic

import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskStatusAssignedToTaskEvent
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.createTaskStatus(name: String): TaskStatusCreatedEvent {
    if (taskStatuses.values.any { it.name == name }) {
        throw IllegalArgumentException("Task status already exists: $name")
    }
    return TaskStatusCreatedEvent(projectId = this.getId(), taskStatusId = UUID.randomUUID(), taskStatusName = name)
}

fun ProjectAggregateState.assignTaskStatusToTask(taskStatusId: UUID, taskId: UUID): TaskStatusAssignedToTaskEvent {
    if (!taskStatuses.containsKey(taskStatusId)) {
        throw IllegalArgumentException("Task status doesn't exists: $taskStatusId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TaskStatusAssignedToTaskEvent(projectId = this.getId(), taskStatusId = taskStatusId, taskId = taskId)
}