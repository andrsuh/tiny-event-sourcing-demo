package ru.quipy.logic

import ru.quipy.api.*

import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = UUID.randomUUID(),
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.addParticipant(participantId: UUID): ParticipantAddedEvent {
    if (participantIds.contains(participantId)) {
        throw IllegalArgumentException("Participant with id=$participantId is already in the project")
    }
    return ParticipantAddedEvent(
        projectId = getId(),
        participantId = participantId,
    )
}

fun ProjectAggregateState.updateTitle(updatedTitle: String): ProjectTitleUpdated {
    if (title == updatedTitle) {
        throw IllegalArgumentException("New title is not different")
    }
    return ProjectTitleUpdated(
        projectId = getId(),
        updatedTitle = updatedTitle,
    )
}

fun ProjectAggregateState.createStatus(title: String, color: String, numberOfTaskInStatus: Int): StatusCreatedEvent {
    if (projectStatuses.values.any { it.title == title }) {
        throw IllegalArgumentException("Status already exists: $title")
    }
    return StatusCreatedEvent(
        projectId = getId(),
        statusId = UUID.randomUUID(),
        title = title,
        color = color,
        numberOfTaskInStatus = numberOfTaskInStatus,
    )
}

fun ProjectAggregateState.deleteStatus(statusId: UUID): StatusDeletedEvent {
    if (projectStatuses.keys.none { it == statusId }) {
        throw IllegalArgumentException("Status not exists: $statusId")
    }
    return StatusDeletedEvent(
        projectId = getId(),
        statusId = statusId
    )
}
