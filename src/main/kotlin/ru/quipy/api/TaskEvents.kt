package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_TITLE_CHANGED_EVENT = "TASK_TITLE_CHANGED_EVENT"
const val PARTICIPANT_ASSIGNED_TO_TASK_EVENT = "PARTICIPANT_ASSIGNED_TO_TASK_EVENT"
const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"

@DomainEvent(TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val projectId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(TASK_TITLE_CHANGED_EVENT)
class TaskTitleChangedEvent(
    val taskId: UUID,
    val updatedTitle: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = TASK_TITLE_CHANGED_EVENT,
    createdAt = createdAt
)

@DomainEvent(PARTICIPANT_ASSIGNED_TO_TASK_EVENT)
class ParticipantAssignedToTaskEvent(
    val taskId: UUID,
    val participantId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = PARTICIPANT_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)

@DomainEvent(TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangedEvent(
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<TaskAggregate>(
    name = TASK_STATUS_CHANGED_EVENT,
    createdAt = createdAt
)
