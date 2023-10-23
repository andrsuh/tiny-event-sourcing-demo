package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_TITLE_CHANGED_EVENT = "TASK_TITLE_CHANGED_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STAUTS_CHANGED_EVENT"
const val ASSIGNED_EXCUTOR_TO_TASK_EVENT = "ASSIGNED_EXCUTOR_TO_TASK_EVENT"

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    val status: UUID,
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TASK_TITLE_CHANGED_EVENT)
class TaskNameChangeEvent(
    val taskId: UUID,
    val taskName: String,
) : Event<TaskAggregate>(
    name = TASK_TITLE_CHANGED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangeEvent(
    val taskId: UUID,
    val statusId: UUID,
) : Event<TaskAggregate>(
    name = TASK_STATUS_CHANGED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = ASSIGNED_TAG_TO_TASK_EVENT)
class AssignedExcutorToTaskEvent(
    val taskId: UUID,
    val userId: UUID,
) : Event<TaskAggregate>(
    name = ASSIGNED_TAG_TO_TASK_EVENT,
    createdAt = createdAt
)

