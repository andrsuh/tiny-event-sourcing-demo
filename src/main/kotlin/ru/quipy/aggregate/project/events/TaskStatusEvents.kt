package ru.quipy.aggregate.project.events

import ru.quipy.aggregate.project.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val TASK_STATUS_CREATED_EVENT = "TASK_STATUS_CREATED_EVENT"
const val TASK_STATUS_DELETED_EVENT = "TASK_STATUS_DELETED_EVENT"

@DomainEvent(TASK_STATUS_CREATED_EVENT)
class TaskStatusCreatedEvent(
    val taskStatusId: UUID,
    val projectId: UUID,
    val title: String,
    val colorRgb: Int,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_STATUS_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(TASK_STATUS_DELETED_EVENT)
class TaskStatusDeletedEvent(
    val taskStatusId: UUID,
    val projectId: UUID,
    createdAt: Long = System.currentTimeMillis()
) : Event<ProjectAggregate>(
    name = TASK_STATUS_DELETED_EVENT,
    createdAt = createdAt
)