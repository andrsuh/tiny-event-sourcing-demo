package ru.quipy.commands.projectManagment.project

import ru.quipy.events.projectManagment.project.StatusRemovedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

fun ProjectAggregateState.removeStatus(statusId: UUID): StatusRemovedEvent {
    if (!this.project.statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Project doesn't have status with id $statusId")
    }
    if (project.tasks.values.any { task -> task.statusId == statusId }) {
        throw IllegalArgumentException("Status with id $statusId has any task")
    }
    return StatusRemovedEvent(
        this.getId(),
        statusId,
    )
}