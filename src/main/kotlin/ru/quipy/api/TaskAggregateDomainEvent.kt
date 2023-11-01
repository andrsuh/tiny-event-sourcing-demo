package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.TaskStatus
import java.util.*

const val TASK_CREATED_EVENT = "USER_CREATED_EVENT"
const val TASK_DELETED_EVENT = "USER_DELETED_EVENT"
const val TASK_TITLE_UPDATED_EVENT = "TASK_TITLE_UPDATED_EVENT"

// API
@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val projectId: UUID,
    val creatorId: UUID,
    val title: String,
    val description: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_TITLE_UPDATED_EVENT)
class TaskTitleUpdatedEvent(
    val taskId: UUID,
    val updaterId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_TITLE_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskStatusUpdatedEvent(
    val taskId: UUID,
    val updaterId: UUID,
    val status: TaskStatus,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt,
)


@DomainEvent(name = TASK_DELETED_EVENT)
class TaskDeletedEvent(
    val taskId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
    name = TASK_DELETED_EVENT,
    createdAt = createdAt,
)