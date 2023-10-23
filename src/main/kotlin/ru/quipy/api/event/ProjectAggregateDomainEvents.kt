package ru.quipy.api.event

import ru.quipy.api.aggregate.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_MEMBER_ADDED_EVENT = "PROJECT_MEMBER_ADDED_EVENT"
const val PROJECT_TASK_CREATED_EVENT = "PROJECT_TASK_CREATED_EVENT"
const val PROJECT_TASK_TITLE_CHANGED_EVENT = "PROJECT_TASK_TITLE_CHANGED_EVENT"
const val PROJECT_TASK_STATUS_CHANGED_EVENT = "PROJECT_TASK_STATUS_CHANGED_EVENT"
const val PROJECT_TASK_MEMBER_ASSIGNED_EVENT = "PROJECT_TASK_MEMBER_ASSIGNED_EVENT"
const val PROJECT_TITLE_CHANGED_EVENT = "PROJECT_TITLE_CHANGED_EVENT"
const val PROJECT_STATUS_ADDED_EVENT = "PROJECT_STATUS_ADDED_EVENT"
const val PROJECT_STATUS_DELETED_EVENT = "PROJECT_STATUS_DELETED_EVENT"


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

@DomainEvent(name = PROJECT_MEMBER_ADDED_EVENT)
class ProjectMemberAddedEvent(
    val projectId: UUID,
    val memberId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_ADDED_EVENT,
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

@DomainEvent(name = PROJECT_STATUS_ADDED_EVENT)
class ProjectStatusAddedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val statusName: String,
    val statusColor: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_STATUS_ADDED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_STATUS_DELETED_EVENT)
class ProjectStatusDeletedEvent(
    val projectId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_STATUS_DELETED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_TASK_CREATED_EVENT)
class ProjectTaskCreatedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val taskName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PROJECT_TASK_MEMBER_ASSIGNED_EVENT)
class ProjectTaskMemberAssignedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val memberId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_TASK_MEMBER_ASSIGNED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_TASK_STATUS_CHANGED_EVENT)
class ProjectTaskStatusChangedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_TASK_STATUS_CHANGED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = PROJECT_TASK_TITLE_CHANGED_EVENT)
class ProjectTaskTitleChangedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val title: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_TASK_TITLE_CHANGED_EVENT,
    createdAt = createdAt
)