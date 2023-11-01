package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.*

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

// Create Project
fun UserAggregateState.create(id: UUID, surname: String, username: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userId = id,
        surname = surname,
        username = username,
        password = password,
    )
}