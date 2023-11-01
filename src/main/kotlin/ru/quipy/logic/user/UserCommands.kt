package ru.quipy.logic.user

import ru.quipy.aggregate.user.events.UserChangedNameEvent
import ru.quipy.aggregate.user.events.UserCreatedEvent
import java.util.*

fun UserAggregateState.createUser(userName: String): UserCreatedEvent {
    return UserCreatedEvent(UUID.randomUUID(), userName)
}

fun UserAggregateState.changeUserName(userName: String, userId: UUID): UserChangedNameEvent {
    return UserChangedNameEvent(userId, userName)
}