package ru.quipy.task.eda.api

import ru.quipy.task.eda.api.TaskAggregate

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val taskName: String,
    createdAt: Long = System.currentTimeMillis(),
    updatedAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

const val TASK_UPDATED_EVENT = "TASK_UPDATED_EVENT"

@DomainEvent(name = TASK_UPDATED_EVENT)
class TaskUpdatedEvent(
    val taskId: UUID,
    val newTaskName: String,
    createdAt: Long = System.currentTimeMillis(),
    updatedAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = TASK_UPDATED_EVENT,
    createdAt = createdAt
)

const val TASK_ASSIGNED_EVENT = "TASK_ASSIGNED_EVENT"

@DomainEvent(name = TASK_ASSIGNED_EVENT)
class TaskAssignedEvent(
    val userId: UUID,
    val taskId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = TASK_ASSIGNED_EVENT,
    createdAt = createdAt
)