package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.*

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

// Create Project
fun UserAggregateState.create(id: UUID, username: String, nickname: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
            userId = id,
            username = username,
            nickname = nickname,
            password = password,
    )
}