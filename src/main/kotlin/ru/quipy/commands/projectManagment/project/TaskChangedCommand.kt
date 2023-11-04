package ru.quipy.commands.projectManagment.project

import ru.quipy.events.projectManagment.project.TaskChangedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

fun ProjectAggregateState.changeTask(taskId: UUID, newTaskName: String?, newStatusId: UUID?): TaskChangedEvent {
    if (newStatusId == null &&
        newTaskName == null
    ) {
        throw IllegalArgumentException("All properties cannot be null while updating")
    }
    if (!this.project.tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Project doesn't have task with id $taskId")
    }
    return TaskChangedEvent(
        this.getId(),
        taskId,
        newTaskName,
        newStatusId,
    )
}