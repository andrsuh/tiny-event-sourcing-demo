package ru.quipy.events.projectManagment.task

import ru.quipy.aggregates.projectManagment.TaskAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val TASK_RENAMED_EVENT = "TASK_RENAMED_EVENT"

@DomainEvent(name = TASK_RENAMED_EVENT)
class TaskRenamedEvent (
    val taskId : UUID,
    val newName : String,
    createdAt : Long = System.currentTimeMillis(),
) : Event<TaskAggregate> (
    name = TASK_RENAMED_EVENT,
    createdAt = createdAt,
)