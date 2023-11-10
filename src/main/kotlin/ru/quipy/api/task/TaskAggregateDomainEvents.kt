package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val STATUS_ASSIGNED_TO_TASK_EVENT = "STATUS_ASSIGNED_TO_TASK_EVENT"
const val TASK_TITLE_CHANGED_EVENT = "TASK_TITLE_CHANGED_EVENT"
const val TASK_ADDED_EXECUTOR_EVENT = "TASK_ADDED_EXECUTOR_EVENT"
const val TASK_REMOVED_EXECUTOR_EVENT = "TASK_REMOVED_EXECUTOR_EVENT"

// API
@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val projectId: UUID,
    val title: String,
    val creatorId: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STATUS_ASSIGNED_TO_TASK_EVENT)
class StatusAssignedToTaskEvent(
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = STATUS_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_TITLE_CHANGED_EVENT)
class TaskTitleChangedEvent(
    val taskId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_ADDED_EXECUTOR_EVENT)
class TaskAddedExecutorEvent(
        val taskId: UUID,
        val executorId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = TASK_ADDED_EXECUTOR_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = TASK_REMOVED_EXECUTOR_EVENT)
class TaskRemovedExecutor(
        val taskId: UUID,
        val executorId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = TASK_REMOVED_EXECUTOR_EVENT,
        createdAt = createdAt,
)


