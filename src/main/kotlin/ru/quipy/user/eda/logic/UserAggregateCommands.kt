package ru.quipy.user.eda.logic

import ru.quipy.user.eda.api.UserCreatedEvent
import java.util.*

fun UserAggregateState.create(
    id: UUID, username: String, realName: String, password: String
): UserCreatedEvent {
    return UserCreatedEvent(id, username, realName, password)
}