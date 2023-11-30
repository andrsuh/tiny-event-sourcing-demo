package ru.quipy.events.projectManagment.project

import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val TASK_CHANGED_EVENT = "TASK_CHANGED_EVENT"

@DomainEvent(name = TASK_CHANGED_EVENT)
class TaskChangedEvent(
    val projectId: UUID,
    val taskId: UUID,
    val newTaskName: String?,
    val newStatusId: UUID?,
    createdAt: Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_CHANGED_EVENT,
    createdAt = createdAt,
)