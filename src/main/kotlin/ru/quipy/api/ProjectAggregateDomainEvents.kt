package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.Status
import ru.quipy.logic.StatusColor
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val TAG_CREATED_EVENT = "TAG_CREATED_EVENT"
const val TAG_ASSIGNED_TO_TASK_EVENT = "TAG_ASSIGNED_TO_TASK_EVENT"
const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val createdBy: UUID,
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
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = TASK_CREATED_EVENT)
class TaskCreatedEvent(
    val taskId: UUID,
    val taskName: String,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusCreatedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val statusColor: StatusColor,
    val statusValue: Status,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = STATUS_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = TAG_ASSIGNED_TO_TASK_EVENT)
class TagAssignedToTaskEvent(
    val projectId: UUID,
    val taskId: UUID,
    val tagId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TAG_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)


const val MEMBER_ADDED_TO_PROJECT_EVENT = "MEMBER_ADDED_TO_PROJECT_EVENT"
@DomainEvent(name = MEMBER_ADDED_TO_PROJECT_EVENT)
class ParticipantAddedToProjectEvent(
    val projectId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = MEMBER_ADDED_TO_PROJECT_EVENT,
    createdAt = createdAt
)

const val TASK_RENAMED_EVENT = "TASK_RENAMED_EVENT"
@DomainEvent(name = TASK_RENAMED_EVENT)
class TaskRenamedEvent(
    val taskId: UUID,
    val newName: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_RENAMED_EVENT,
    createdAt = createdAt
)

const val TASK_ASSIGNED_TO_USER_EVENT = "TASK_ASSIGNED_TO_USER_EVENT"
@DomainEvent(name = TASK_ASSIGNED_TO_USER_EVENT)
class TaskAssignedToUserEvent(
    val taskId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_ASSIGNED_TO_USER_EVENT,
    createdAt = createdAt
)

const val TASK_SELF_ASSIGNED_EVENT = "TASK_SELF_ASSIGNED_EVENT"
@DomainEvent(name = TASK_SELF_ASSIGNED_EVENT)
class TaskSelfAssignedEvent(
    val taskId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_SELF_ASSIGNED_EVENT,
    createdAt = createdAt
)

const val STATUS_ASSIGNED_TO_TASK_EVENT = "STATUS_ASSIGNED_TO_TASK_EVENT"
@DomainEvent(name = STATUS_ASSIGNED_TO_TASK_EVENT)
class StatusAssignedToTaskEvent(
    val taskId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = STATUS_ASSIGNED_TO_TASK_EVENT,
    createdAt = createdAt
)

const val TASK_STATUS_CHANGED_EVENT = "TASK_STATUS_CHANGED_EVENT"
@DomainEvent(name = TASK_STATUS_CHANGED_EVENT)
class TaskStatusChangedEvent(
    val taskId: UUID,
    val newStatusId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_STATUS_CHANGED_EVENT,
    createdAt = createdAt
)

const val STATUS_DELETED_EVENT = "STATUS_DELETED_EVENT"
@DomainEvent(name = STATUS_DELETED_EVENT)
class StatusDeletedEvent(
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = STATUS_DELETED_EVENT,
    createdAt = createdAt
)
const val TASK_DELETED_EVENT = "TASK_DELETED_EVENT"

@DomainEvent(name = TASK_DELETED_EVENT)
class TaskDeletedEvent(
    val taskId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_DELETED_EVENT,
    createdAt = createdAt
)
const val PROJECT_DELETED_EVENT = "PROJECT_DELETED_EVENT"

@DomainEvent(name = PROJECT_DELETED_EVENT)
class ProjectDeletedEvent(
    val projectId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = PROJECT_DELETED_EVENT,
    createdAt = createdAt
)

