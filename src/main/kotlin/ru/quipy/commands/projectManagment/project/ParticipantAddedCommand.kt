package ru.quipy.commands.projectManagment.project

import ru.quipy.events.projectManagment.project.ParticipantAddedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

fun ProjectAggregateState.addParticipant(userId: UUID): ParticipantAddedEvent {
    if (this.project.participants.contains(userId)) {
        throw IllegalArgumentException("Project already has task with id $userId")
    }

    return ParticipantAddedEvent(
        this.getId(),
        userId,
    )
}