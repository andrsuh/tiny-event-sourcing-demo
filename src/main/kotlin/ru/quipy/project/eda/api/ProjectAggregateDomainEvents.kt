package ru.quipy.project.eda.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_UPDATED_EVENT = "PROJECT_UPDATED_EVENT"
const val PROJECT_PARTICIPANT_ADDED_EVENT = "PROJECT_PARTICIPANT_ADDED_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val projectName: String,
    val projectOwner: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_UPDATED_EVENT)
class ProjectUpdatedEvent(
    val projectId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_UPDATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_PARTICIPANT_ADDED_EVENT)
class ProjectParticipantAddedEvent(
    val projectId: UUID,
    val participantId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_PARTICIPANT_ADDED_EVENT,
    createdAt = createdAt,
)