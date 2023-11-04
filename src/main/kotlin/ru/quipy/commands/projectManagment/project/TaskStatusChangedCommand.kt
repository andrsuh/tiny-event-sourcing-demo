package ru.quipy.commands.projectManagment.project

import ru.quipy.events.projectManagment.project.TaskStatusChangedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

fun ProjectAggregateState.changeTaskStatus(taskId: UUID, statusId: UUID) : TaskStatusChangedEvent {
    if (!this.statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Project doesn't have status with id $statusId")
    }
    if (!this.tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Project doesn't have task with id $taskId")
    }
    return TaskStatusChangedEvent(
        this.getId(),
        taskId,
        statusId,
    )
}