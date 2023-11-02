package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_NAME_CHANGED_EVENT = "TASK_NAME_CHANGED_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"
const val TASK_EXECUTOR_CHANGED_EVENT = "TASK_EXECUTOR_CHANGED_EVENT"
const val TAG_ASSIGNED_TO_TASK_EVENT = "TAG_ASSIGNED_TO_TASK_EVENT"

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
        val taskId: UUID,
        val projectId: UUID,
        val taskName: String,
        val creatorId: UUID,
        val tagId: UUID,
        var executors: List<UUID> = emptyList(),
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = TASK_CREATED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = TASK_NAME_CHANGED_EVENT)
class TaskNameChangedEvent(
        val taskId: UUID,
        val projectId: UUID,
        val newTaskName: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = TASK_NAME_CHANGED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangedEvent(
        val taskId: UUID,
        val projectId: UUID,
        val statusId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = TASK_STATUS_CHANGED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = TASK_EXECUTOR_CHANGED_EVENT)
class TaskExecutorChangedEvent(
        val taskId: UUID,
        val projectId: UUID,
        val userId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = TASK_EXECUTOR_CHANGED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = TAG_ASSIGNED_TO_TASK_EVENT)
class TagAssignedToTaskEvent(
        val projectId: UUID,
        val taskId: UUID,
        val tagId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = TAG_ASSIGNED_TO_TASK_EVENT,
        createdAt = createdAt
)