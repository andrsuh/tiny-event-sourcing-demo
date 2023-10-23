package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val STATUS_SET_EVENT = "STATUS_SET_EVENT"
const val TASK_EXECUTOR_ADDED_EVENT = "TASK_EXECUTOR_ADDED_EVENT"
const val TASK_EXECUTOR_REMOVED_EVENT = "TASK_EXECUTOR_REMOVED_EVENT"

// API
@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val taskTitle: String,
    val description: String,
    val projectId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = STATUS_SET_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STATUS_SET_EVENT)
class StatusSetEvent(
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = STATUS_SET_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_EXECUTOR_ADDED_EVENT)
class TaskExecutorAddedEvent(
    val taskId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = STATUS_SET_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_EXECUTOR_REMOVED_EVENT)
class TaskExecutorRemovedEvent(
    val taskId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = STATUS_SET_EVENT,
    createdAt = createdAt,
)
