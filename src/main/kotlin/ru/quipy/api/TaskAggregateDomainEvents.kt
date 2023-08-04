package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

private const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
private const val TASK_RENAMED_EVENT = "TASK_RENAMED_EVENT"
private const val TASK_ASSIGNED_EVENT = "TASK_ASSIGNED_EVENT"
private const val TASK_STATUS_SET_EVENT = "TASK_STATUS_SET_EVENT"

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val projectId: UUID,
    val taskName: String,
    val creatorId: UUID,
    val statusId: UUID,
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
)
@DomainEvent(name = TASK_RENAMED_EVENT)
class TaskRenamedEvent(
    val taskId: UUID,
    val newName: String,
) : Event<TaskAggregate>(
    name = TASK_RENAMED_EVENT,
)

@DomainEvent(name = TASK_ASSIGNED_EVENT)
class TaskAssignedEvent(
    val taskId: UUID,
    val assigneeId: UUID,
) : Event<TaskAggregate>(
    name = TASK_ASSIGNED_EVENT,
)

@DomainEvent(name = TASK_STATUS_SET_EVENT)
class TaskStatusSetEvent(
    val taskId: UUID,
    val statusId: UUID,
) : Event<TaskAggregate>(
    name = TASK_STATUS_SET_EVENT,
)
