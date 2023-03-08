package ru.quipy.logic

import ru.quipy.api.ExecutorAssignedToTaskEvent
import ru.quipy.api.ExecutorRetractedFromTaskEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAssignedToTaskEvent
import java.util.*

fun TaskAggregateState.create(
    projectId: UUID,
    taskId: UUID = UUID.randomUUID(),
    taskName: String,
    creatorId: UUID,
    defaultTaskStatusId: UUID,
): TaskCreatedEvent =
    TaskCreatedEvent(
        projectId = projectId,
        taskId = taskId,
        taskName = taskName,
        creatorId = creatorId,
        defaultTaskStatusId = defaultTaskStatusId,
    )

fun TaskAggregateState.assignTaskStatus(projectId: UUID, taskId: UUID, taskStatusId: UUID): TaskStatusAssignedToTaskEvent {
    if (this.projectId != projectId) {
        throw IllegalStateException("Invalid project id: $projectId")
    }

    return TaskStatusAssignedToTaskEvent(projectId = projectId, taskId = taskId, taskStatusId = taskStatusId)
}

fun TaskAggregateState.assignExecutor(projectId: UUID, taskId: UUID, executorId: UUID): ExecutorAssignedToTaskEvent =
    ExecutorAssignedToTaskEvent(projectId = projectId, taskId = taskId, executorId = executorId)

fun TaskAggregateState.retractExecutor(projectId: UUID, taskId: UUID, executorId: UUID): ExecutorRetractedFromTaskEvent =
    ExecutorRetractedFromTaskEvent(projectId = projectId, taskId = taskId, executorId = executorId)