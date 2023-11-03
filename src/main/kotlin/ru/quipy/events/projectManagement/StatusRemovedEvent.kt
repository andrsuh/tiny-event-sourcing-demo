package ru.quipy.events.projectManagement

import ru.quipy.api.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

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