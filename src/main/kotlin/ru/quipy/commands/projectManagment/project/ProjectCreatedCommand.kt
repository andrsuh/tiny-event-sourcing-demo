package ru.quipy.commands.projectManagment.project

import ru.quipy.events.projectManagment.project.ProjectCreatedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

fun ProjectAggregateState.create(id: UUID, name: String, creatorId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        id,
        name,
        creatorId,
    )
}