package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.*

fun UserAggregateState.createUser(nickname: String, userName: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(UUID.randomUUID(), nickname, userName, password)
}
