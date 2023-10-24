package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.createProject(id: UUID, name: String, creatorId: UUID, description : String): ProjectCreatedEvent {
    if (name.length > 255) {
        throw IllegalArgumentException("Project name should be less than 255 characters!")
    }

    return ProjectCreatedEvent(
        projectId = id,
        projectName = name,
        creatorId = creatorId,
        description = description
    )
}

fun ProjectAggregateState.addParticipantById(userId:UUID): ParticipantAddedEvent {
    return ParticipantAddedEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.leaveProject(userId: UUID): LeaveProjectEvent {
    return LeaveProjectEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.createStatus(statusId:UUID, statusName: String, colour: String): StatusCreatedEvent {
    if (statusName.length > 255) {
        throw IllegalArgumentException("Status name should be less than 255 characters!")
    }

    return StatusCreatedEvent(projectId = this.getId(), statusId = statusId, statusName = statusName, colour = colour)
}

fun ProjectAggregateState.deleteStatus(statusId: UUID): StatusDeletedEvent {
    return StatusDeletedEvent(projectId = this.getId(), statusId = statusId)
}