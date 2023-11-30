package ru.quipy.events.projectManagment.project

import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val STATUS_ADDED_EVENT = "STATUS_ADDED_EVENT"

@DomainEvent(name = STATUS_ADDED_EVENT)
class StatusAddedEvent(
    val projectId: UUID,
    val statusId: UUID,
    val statusName: String,
    val colorCode: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = STATUS_ADDED_EVENT,
    createdAt = createdAt,
)