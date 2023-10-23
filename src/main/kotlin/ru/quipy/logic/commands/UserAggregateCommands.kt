package ru.quipy.logic.commands

import ru.quipy.api.event.*
import ru.quipy.logic.state.UserAggregateState
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun UserAggregateState.create(id: UUID, name: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userId = id,
        userName = name,
        userPassword = password,
    )
}

fun UserAggregateState.changeName(name: String): UserNameChangedEvent {
    return UserNameChangedEvent(userId = this.getId(), userName = name)
}

fun UserAggregateState.addProject(projectId: UUID): UserProjectAddedEvent {
    if (projects.values.any { it == projectId }) {
        throw IllegalArgumentException("Status already exists: $projectId")
    }
    return UserProjectAddedEvent(userId = this.getId(), projectId = projectId)
}