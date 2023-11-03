package ru.quipy.events.projectManagement

import ru.quipy.api.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.awt.Color
import java.util.*

const val STATUS_ADDED_EVENT = "STATUS_ADDED_EVENT"

@DomainEvent(name = STATUS_ADDED_EVENT)
class StatusAddedEvent(
    val statusId: UUID,
    val projectId: UUID,
    val statusName: String,
    val color: Color,
    createdAt: Long = System.currentTimeMillis(),
    ) : Event<ProjectAggregate>(
    name = STATUS_ADDED_EVENT,
    createdAt = createdAt,
)