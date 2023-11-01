package ru.quipy.logic.project

import ru.quipy.aggregate.project.events.ProjectAddedMemberEvent
import ru.quipy.aggregate.project.events.ProjectCreatedEvent
import ru.quipy.aggregate.project.events.ProjectRenamedEvent
import ru.quipy.logic.UserAccessConstraintChecker.Companion.canAccess
import java.util.*

fun ProjectAggregateState.create(userId: UUID, title: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(UUID.randomUUID(), title, userId, UUID.randomUUID())
}

fun ProjectAggregateState.rename(callerId: UUID, title: String): ProjectRenamedEvent {
    canAccess(this, callerId)

    if (this.title == title) {
        throw IllegalArgumentException("Project title should be different")
    }

    return ProjectRenamedEvent(getId(), title)
}

fun ProjectAggregateState.addMember(userId: UUID, callerId: UUID): ProjectAddedMemberEvent {
    canAccess(this, callerId)

    if (memberIds.contains(userId)) {
        throw IllegalArgumentException("User with id=$userId is already in the project")
    }

    return ProjectAddedMemberEvent(getId(), userId)
}