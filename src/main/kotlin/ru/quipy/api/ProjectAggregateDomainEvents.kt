package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val TAG_CREATED_EVENT = "TAG_CREATED_EVENT"
const val USER_ASSIGNED_TO_PROJECT_EVENT = "USER_ASSIGNED_TO_PROJECT_EVENT"
const val TAG_CHANGE_NAME_EVENT = "TAG_CHANGE_NAME_EVENT"
const val TAG_CHANGE_COLOR_EVENT = "TAG_CHANGE_COLOR_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
        val projectId: UUID,
        val title: String,
        val creatorId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = PROJECT_CREATED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = TAG_CREATED_EVENT)
class TagCreatedEvent(
        val projectId: UUID,
        val tagId: UUID,
        val tagName: String,
        val color: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = TAG_CREATED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = USER_ASSIGNED_TO_PROJECT_EVENT)
class UserAssignedToProjectEvent(
        val projectId: UUID,
        val userId: UUID,
        val username: String,
        val nickname: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = USER_ASSIGNED_TO_PROJECT_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = TAG_CHANGE_NAME_EVENT)
class TagChangeNameEvent(
        val projectId: UUID,
        val tagId: UUID,
        val tagName: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = TAG_CHANGE_NAME_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = TAG_CHANGE_COLOR_EVENT)
class TagChangeColorEvent(
        val projectId: UUID,
        val tagId: UUID,
        val color: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = TAG_CHANGE_COLOR_EVENT,
        createdAt = createdAt,
)