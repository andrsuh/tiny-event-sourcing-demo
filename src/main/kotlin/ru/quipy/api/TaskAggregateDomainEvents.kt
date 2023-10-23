package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val TASKK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_TITLE_CHANGED_EVENT = "TASK_TITLE_CHANGED_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"
const val ASSIGNED_EXCUTOR_TO_TASK_EVENT = "ASSIGNED_EXCUTOR_TO_TASK_EVENT"

@DomainEvent(name = TASKK_CREATED_EVENT)
class TaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    val statusId: UUID,
) : Event<TaskAggregate>(
    name = TASKK_CREATED_EVENT
)

@DomainEvent(name = TASK_TITLE_CHANGED_EVENT)
class TaskNameChangeEvent(
    val taskId: UUID,
    val taskName: String,
) : Event<TaskAggregate>(
    name = TASK_TITLE_CHANGED_EVENT
)

@DomainEvent(name = TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangeEvent(
    val taskId: UUID,
    val statusId: UUID,
    val oldStatusId: UUID,
) : Event<TaskAggregate>(
    name = TASK_STATUS_CHANGED_EVENT
)

@DomainEvent(name = ASSIGNED_EXCUTOR_TO_TASK_EVENT)
class AssignedExcutorToTaskEvent(
    val taskId: UUID,
    val userId: UUID,
) : Event<TaskAggregate>(
    name = ASSIGNED_EXCUTOR_TO_TASK_EVENT
)

