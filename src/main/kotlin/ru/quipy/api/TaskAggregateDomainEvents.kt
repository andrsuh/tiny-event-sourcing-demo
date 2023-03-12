package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.StatusEntity
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val EXECUTOR_ADDED_EVENT = "EXECUTOR_ADDED_EVENT"
const val EXECUTOR_DELETED_EVENT = "EXECUTOR_DELETED_EVENT"
const val TASK_RENAMED_EVENT = "TASK_CREATED_EVENT"
const val STATUS_SET_EVENT = "STATUS_SET_EVENT"

// API
@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
        val taskId: UUID,
        val taskName: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = TASK_CREATED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = EXECUTOR_ADDED_EVENT)
class ExecutorAddedEvent(
        val taskId: UUID,
        val executorName: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = EXECUTOR_ADDED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = EXECUTOR_DELETED_EVENT)
class ExecutorDeletedEvent(
        val taskId: UUID,
        val executorName: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = EXECUTOR_DELETED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = TASK_RENAMED_EVENT)
class TaskRenamedEvent(
        val taskId: UUID,
        val taskName: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = TASK_RENAMED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = STATUS_SET_EVENT)
class StatusSetEvent(
        val taskId: UUID,
        val status: StatusEntity,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = STATUS_SET_EVENT,
        createdAt = createdAt,
)