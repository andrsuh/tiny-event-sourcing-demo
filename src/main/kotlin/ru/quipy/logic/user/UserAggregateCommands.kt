package ru.quipy.logic.user

import ru.quipy.api.user.UserCreatedEvent
import ru.quipy.logic.UserAggregateState
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun UserAggregateState.create(id: UUID, name: String, nickname: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userId = id,
        userName = name,
        userNickName = nickname,
        userPassword = password
    )
}
