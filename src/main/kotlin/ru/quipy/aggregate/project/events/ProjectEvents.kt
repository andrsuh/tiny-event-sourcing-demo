package ru.quipy.aggregate.project.events

import ru.quipy.aggregate.project.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_RENAMED_EVENT = "PROJECT_RENAMED_EVENT"
const val PROJECT_MEMBER_ADDED_EVENT = "PROJECT_MEMBER_ADDED_EVENT"

@DomainEvent(PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: UUID,
    val defaultStatusId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(PROJECT_RENAMED_EVENT)
class ProjectRenamedEvent(
    val projectId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = PROJECT_RENAMED_EVENT,
    createdAt = createdAt
)

@DomainEvent(PROJECT_MEMBER_ADDED_EVENT)
class ProjectAddedMemberEvent(
    val projectId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_ADDED_EVENT,
    createdAt = createdAt
)
