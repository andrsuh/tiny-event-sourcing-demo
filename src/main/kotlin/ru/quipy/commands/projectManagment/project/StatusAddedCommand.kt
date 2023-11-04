package ru.quipy.commands.projectManagment.project

import ru.quipy.events.projectManagment.project.StatusAddedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.awt.Color
import java.util.UUID

fun ProjectAggregateState.addStatus(statusId: UUID, statusName: String, statusColor: Color) : StatusAddedEvent {
    if (this.statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Project already has status with id $statusId")
    }
    return StatusAddedEvent(
        this.getId(),
        statusId,
        statusName,
        statusColor,
    )
}