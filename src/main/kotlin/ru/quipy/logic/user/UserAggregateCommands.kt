package ru.quipy.logic.user

import ru.quipy.api.user.UserCreatedEvent
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun UserAggregateState.createUser(userId: UUID, nickname: String, name: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userId = UUID.randomUUID(),
        nickname = nickname,
        userName = name,
        password = password
    )
}
