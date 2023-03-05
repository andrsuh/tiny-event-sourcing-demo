package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_STATUS_ASSIGNED_TO_TASK_EVENT = "TASK_STATUS_ASSIGNED_TO_TASK_EVENT"
const val EXECUTOR_ASSIGNED_TO_TASK_EVENT = "EXECUTOR_ASSIGNED_TO_TASK_EVENT"
const val EXECUTOR_RETRACTED_FROM_TASK_EVENT = "EXECUTOR_RETRACTED_FROM_TASK_EVENT"

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    val creatorId: UUID,
    val defaultTaskStatusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_ASSIGNED_TO_TASK_EVENT)
class TaskStatusAssignedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskStatusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_STATUS_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = EXECUTOR_ASSIGNED_TO_TASK_EVENT)
class ExecutorAssignedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val executorId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = EXECUTOR_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = EXECUTOR_RETRACTED_FROM_TASK_EVENT)
class ExecutorRetractedFromTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val executorId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = EXECUTOR_RETRACTED_FROM_TASK_EVENT,
    createdAt = createdAt,
)