package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.TaskEntity
import java.util.*

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val TAG_CREATED_EVENT = "TAG_CREATED_EVENT"
const val TAG_ASSIGNED_TO_TASK_EVENT = "TAG_ASSIGNED_TO_TASK_EVENT"
//const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
const val TASK_ADDEDED_EVENT = "TASK_ADDEDED_EVENT"
const val STATUS_CREATED_EVENT = "STATUS_CREATED_EVENT"
const val STATUS_DELETED_EVENT = "STATUS_DELETED_EVENT"
const val MEMBER_ADDED_EVENT = "MEMBER_ADDED_EVENT"
const val MEMBER_DELETED_EVENT = "MEMBER_DELETED_EVENT"
const val PROJECT_RENAMED_EVENT = "PROJECT_RENAMED_EVENT"

// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val title: String,
    val creatorId: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = PROJECT_RENAMED_EVENT)
class ProjectRenamedEvent(
        val projectId: UUID,
        val title: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = PROJECT_RENAMED_EVENT,
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

//@DomainEvent(name = TASK_CREATED_EVENT)
//class TaskCreatedEvent(
//    val projectId: UUID,
//    val taskId: UUID,
//    val taskName: String,
//    createdAt: Long = System.currentTimeMillis(),
//) : Event<ProjectAggregate>(
//    name = TASK_CREATED_EVENT,
//    createdAt = createdAt
//)

@DomainEvent(name = TASK_ADDEDED_EVENT)
class TaskAddedEvent(
    val projectId: UUID,
    val task: TaskEntity,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_ADDEDED_EVENT,
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

@DomainEvent(name = STATUS_CREATED_EVENT)
class StatusCreatedEvent(
        val projectId: UUID,
        val statusName: String,
        val color: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = STATUS_CREATED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = STATUS_DELETED_EVENT)
class StatusDeletedEvent(
        val projectId: UUID,
        val statusName: String,
        val color: String,
        createdAt: Long = System.currentTimeMillis(),
) : Event<TaskAggregate>(
        name = STATUS_DELETED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = MEMBER_ADDED_EVENT)
class MemberAddedEvent(
        val projectId: UUID,
        val memberId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = MEMBER_ADDED_EVENT,
        createdAt = createdAt,
)

@DomainEvent(name = MEMBER_DELETED_EVENT)
class MemberDeletedEvent(
        val projectId: UUID,
        val memberId: UUID,
        createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
        name = MEMBER_DELETED_EVENT,
        createdAt = createdAt,
)