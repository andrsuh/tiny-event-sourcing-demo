package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val ADD_PARTICIPANT_TO_PROJECT_EVENT = "ADD_PARTICIPANT_TO_PROJECT_EVENT"
const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"
const val CHANGE_STATUS_EVENT = "CHANGE_STATUS_EVENT"
const val DELETE_STATUS_EVENT = "DELETE_STATUS_EVENT"
const val STAUS_ASSIGNED_TO_TASK_EVENT= "STATUS_ASSIGNED_TO_TASK_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: String,
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT
)

@DomainEvent(name = ADD_PARTICIPANT_TO_PROJECT_EVENT)
class AddParticipantToProjectEvent(
    val projectId: UUID,
    val participantId: UUID,
) : Event<ProjectAggregate>(
    name = ADD_PARTICIPANT_TO_PROJECT_EVENT
)

@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusCreatedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val statusName: String,
    val color: String,
) : Event<ProjectAggregate>(
    name = STATUS_CREATED_EVENT
)

@DomainEvent(name = STAUS_ASSIGNED_TO_TASK_EVENT)
class StatusAssignedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val statusId: UUID,
    val oldStatusId: UUID,
) : Event<ProjectAggregate>(
    name = STAUS_ASSIGNED_TO_TASK_EVENT
)

@DomainEvent(name = CHANGE_STATUS_EVENT)
class ChangeStatusEvent(
    val projectId: UUID,
    val statusId: UUID,
    val statusName: String,
) : Event<ProjectAggregate>(
    name = CHANGE_STATUS_EVENT
)

@DomainEvent(name = DELETE_STATUS_EVENT)
class DeleteStatusEvent(
    val projectId: UUID,
    val statusId: UUID,
) : Event<ProjectAggregate>(
    name = DELETE_STATUS_EVENT
)
