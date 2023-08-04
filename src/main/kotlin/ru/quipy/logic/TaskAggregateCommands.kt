package ru.quipy.logic

import ru.quipy.api.TaskAssignedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskRenamedEvent
import ru.quipy.api.TaskStatusSetEvent
import java.lang.IllegalStateException
import java.util.UUID

fun TaskAggregateState.createTask(
    taskId: UUID,
    projectId: UUID,
    name: String,
    creatorId: UUID,
    statusId: UUID,
): TaskCreatedEvent {
    return TaskCreatedEvent(
        taskId = taskId,
        projectId = projectId,
        taskName = name,
        creatorId = creatorId,
        statusId = statusId,
    )
}

fun TaskAggregateState.renameTask(newName: String): TaskRenamedEvent {
    return TaskRenamedEvent(taskId = getId(), newName = newName)
}

fun TaskAggregateState.assignTask(assigneeId: UUID): TaskAssignedEvent {
    if (assignees.contains(assigneeId)) {
        throw UniqueConstraintViolation("User is already assigned to the task")
    }
    return TaskAssignedEvent(taskId = getId(), assigneeId = assigneeId)
}

fun TaskAggregateState.setTaskStatus(statusId: UUID): TaskStatusSetEvent {
    if (statusId == this.statusId) {
        throw IllegalStateException("The task is already in this status")
    }
    return TaskStatusSetEvent(taskId = getId(), statusId = statusId)
}
