package ru.quipy.commands.projectManagment.project

import ru.quipy.events.projectManagment.project.StatusRemovedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

fun ProjectAggregateState.removeStatus(statusId: UUID): StatusRemovedEvent {
    if (!this.project.statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Project doesn't have status with id $statusId")
    }
    return StatusRemovedEvent(
        this.getId(),
        statusId,
    )
}