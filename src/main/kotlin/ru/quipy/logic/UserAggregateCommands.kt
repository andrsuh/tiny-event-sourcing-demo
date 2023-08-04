package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.UUID

fun UserAggregateState.createProjectUser(
    userId: UUID,
    nickname: String,
    name: String,
    password: String,
): UserCreatedEvent {
    // TODO: check if nickname is not used
    return UserCreatedEvent(
        userId = userId,
        nickname = nickname,
        userName = name,
        password = password,
    )
}
