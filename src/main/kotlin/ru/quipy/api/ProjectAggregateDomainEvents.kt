package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_TITLE_CHANGED_EVENT = "PROJECT_TITLE_CHANGED_EVENT"
const val PROJECT_MEMBER_ADDED_EVENT = "PROJECT_MEMBER_ADDED_EVENT"
const val TASK_STATUS_CREATED_EVENT = "TASK_STATUS_CREATED_EVENT"
const val TASK_STATUS_REMOVED_EVENT = "TASK_STATUS_REMOVED_EVENT"

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

@DomainEvent(name = PROJECT_TITLE_CHANGED_EVENT)
class ProjectTitleChangedEvent(
    val projectId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_TITLE_CHANGED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_MEMBER_ADDED_EVENT)
class ProjectMemberAddedEvent(
    val projectId: UUID,
    val memberId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_ADDED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_CREATED_EVENT)
class TaskStatusCreatedEvent(
    val projectId: UUID,
    val taskStatusId: UUID,
    val taskStatusName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_STATUS_REMOVED_EVENT)
class TaskStatusRemovedEvent(
    val projectId: UUID,
    val taskStatusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_STATUS_REMOVED_EVENT,
    createdAt = createdAt,
)