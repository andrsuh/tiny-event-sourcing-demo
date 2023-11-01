package ru.quipy.aggregate.project.events

import ru.quipy.aggregate.project.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_ADDED_EVENT = "TASK_ADDED_EVENT"
const val TASK_ADDED_EXECUTOR_EVENT = "TASK_ADDED_EXECUTOR_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"
const val TASK_NAME_UPDATED_EVENT = "TASK_NAME_UPDATED_EVENT"

@DomainEvent(TASK_ADDED_EVENT)
class TaskAddedEvent(
    val taskId: UUID,
    val title: String,
    val projectId: UUID,
    val description: String,
    val taskStatusId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_ADDED_EVENT,
    createdAt = createdAt
)

@DomainEvent(TASK_NAME_UPDATED_EVENT)
class TaskNameUpdatedEvent(
    val taskId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_NAME_UPDATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(TASK_ADDED_EXECUTOR_EVENT)
class TaskAddedExecutorEvent(
    val taskId: UUID,
    val projectId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_ADDED_EXECUTOR_EVENT,
    createdAt = createdAt
)

@DomainEvent(TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangedEvent(
    val taskId: UUID,
    val projectId: UUID,
    val taskStatusId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_STATUS_CHANGED_EVENT,
    createdAt = createdAt
)