package ru.quipy.logic

import ru.quipy.api.ExecutorAssignedToTaskEvent
import ru.quipy.api.ExecutorRetractedFromTaskEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAssignedToTaskEvent
import ru.quipy.exception.NotFoundException
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
    if (this.taskStatusId == taskStatusId) {
        throw IllegalStateException("Task status already assigned: $taskStatusId")
    }

    return TaskStatusAssignedToTaskEvent(projectId = projectId, taskId = taskId, taskStatusId = taskStatusId)
}

fun TaskAggregateState.assignExecutor(projectId: UUID, taskId: UUID, executorId: UUID): ExecutorAssignedToTaskEvent {
    if (executorId in executorIds) {
        throw IllegalStateException("Executor already assigned: $executorId")
    }

    return ExecutorAssignedToTaskEvent(projectId = projectId, taskId = taskId, executorId = executorId)
}

fun TaskAggregateState.retractExecutor(projectId: UUID, taskId: UUID, executorId: UUID): ExecutorRetractedFromTaskEvent {
    if (executorId !in executorIds) {
        throw NotFoundException("Task has not such executor: $executorId")
    }

    return ExecutorRetractedFromTaskEvent(projectId = projectId, taskId = taskId, executorId = executorId)
}