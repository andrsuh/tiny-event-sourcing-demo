package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PARTICIPANT_ADDED_EVENT = "PARTICIPANT_ADDED_EVENT"
const val EDIT_PROJECT_TITLE_EVENT = "EDIT_PROJECT_TITLE_EVENT"
const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"
const val STATUS_DELETED_EVENT = "STATUS_DELETED_EVENT"


// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PARTICIPANT_ADDED_EVENT)
class ParticipantAddedEvent(
    val projectId: UUID,
    val participantId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = PARTICIPANT_ADDED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = EDIT_PROJECT_TITLE_EVENT)
class EditProjectTitleEvent(
    val projectId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = EDIT_PROJECT_TITLE_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusCreatedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val statusName: String,
    val color: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = STATUS_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = STATUS_DELETED_EVENT)
class StatusDeletedEvent(
    val projectId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = STATUS_DELETED_EVENT,
    createdAt = createdAt,
)