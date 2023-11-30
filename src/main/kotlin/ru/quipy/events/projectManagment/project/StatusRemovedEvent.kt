package ru.quipy.events.projectManagment.project

import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val STATUS_REMOVED_EVENT = "STATUS_REMOVE_EVENT"

@DomainEvent(name = STATUS_REMOVED_EVENT)
class StatusRemovedEvent(
    val projectId: UUID,
    val statusId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = STATUS_REMOVED_EVENT,
    createdAt = createdAt,
)