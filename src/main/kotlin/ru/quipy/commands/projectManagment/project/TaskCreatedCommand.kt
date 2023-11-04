package ru.quipy.commands.projectManagment.project

import ru.quipy.events.projectManagment.project.TaskCreatedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

fun ProjectAggregateState.addTask(taskId: UUID, taskName: String): TaskCreatedEvent {
    if (this.project.tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Project already has task with id $taskId")
    }
    return TaskCreatedEvent(
        this.getId(),
        taskId,
        taskName,
    )
}