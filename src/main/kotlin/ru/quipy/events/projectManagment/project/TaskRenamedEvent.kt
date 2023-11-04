package ru.quipy.events.projectManagment.project

import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.events.projectManagment.task.TASK_RENAMED_EVENT
import java.util.UUID

@DomainEvent(name = TASK_RENAMED_EVENT)
class TaskRenamedEvent (
    val projectId: UUID,
    val taskId : UUID,
    val newTaskName : String,
    createdAt : Long = System.currentTimeMillis(),
) : Event<ProjectAggregate>(
    name = TASK_RENAMED_EVENT,
    createdAt = createdAt,
)