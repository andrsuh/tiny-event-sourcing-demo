package ru.quipy.project.eda.logic

import ru.quipy.domain.Event
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.project.eda.api.ProjectAggregate
import ru.quipy.project.eda.api.ProjectCreatedEvent
import ru.quipy.project.eda.api.ProjectParticipantAddedEvent
import ru.quipy.project.eda.api.ProjectUpdatedEvent
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, name: String, ownerId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        projectName = name,
        projectOwner = ownerId,
    )
}

fun ProjectAggregateState.addUser(id: UUID): List<Event<ProjectAggregate>> {
    if(participants.contains(id)) {
        throw IllegalArgumentException("User with id: $id already exists in project")
    }
    return ArrayList(
        listOf(
            ProjectParticipantAddedEvent(this.getId(), id),
            ProjectUpdatedEvent(this.getId())
        )
    )
}