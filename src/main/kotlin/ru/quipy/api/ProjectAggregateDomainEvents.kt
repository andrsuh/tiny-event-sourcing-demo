package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.logic.Color
import java.util.*
import kotlin.collections.LinkedHashSet

const val PROJECT_CREATED_EVENT = "PROJECT_CREATED_EVENT"
const val PROJECT_MEMBER_ADDED_EVENT = "PROJECT_MEMBER_ADDED_EVENT"
const val TASK_STATUS_CREATED_EVENT = "TASK_STATUS_CREATED_EVENT"
const val TASK_STATUS_DELETED_EVENT = "TASK_STATUS_DELETED_EVENT"
const val TASK_STATUSES_ORDER_SET_EVENT = "TASK_STATUSES_ORDER_SET_EVENT"


// API
@DomainEvent(name = PROJECT_CREATED_EVENT)
class ProjectCreatedEvent(
    val projectId: UUID,
    val projectName: String,
    val creatorId: UUID,
    val defaultStatusId: UUID
) : Event<ProjectAggregate>(
    name = PROJECT_CREATED_EVENT,
)

@DomainEvent(name = PROJECT_MEMBER_ADDED_EVENT)
class ProjectMemberAddedEvent(
    val projectId: UUID,
    val memberId: UUID,
) : Event<ProjectAggregate>(
    name = PROJECT_MEMBER_ADDED_EVENT,
)

@DomainEvent(name = TASK_STATUS_CREATED_EVENT)
class TaskStatusCreatedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val statusName: String,
    val color: Color,
) : Event<ProjectAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
)

@DomainEvent(name = TASK_STATUS_DELETED_EVENT)
class TaskStatusDeletedEvent(
    val projectId: UUID,
    val statusId: UUID,
) : Event<ProjectAggregate>(
    name = TASK_STATUS_DELETED_EVENT,
)

@DomainEvent(name = TASK_STATUSES_ORDER_SET_EVENT)
class TaskStatusesOrderSetEvent(
    val projectId: UUID,
    val statusesIds: LinkedHashSet<UUID>,
) : Event<ProjectAggregate>(
    name = TASK_STATUSES_ORDER_SET_EVENT,
)
