package ru.quipy.events.projectManagment.project

import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val ASSIGNEE_ADDED_EVENT = "ASSIGNEE_ADDED_EVENT"

@DomainEvent(name = ASSIGNEE_ADDED_EVENT)
class AssigneeAddedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val userId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = ASSIGNEE_ADDED_EVENT,
    createdAt = createdAt,
)
