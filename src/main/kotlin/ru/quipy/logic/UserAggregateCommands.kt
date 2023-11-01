package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.*


fun UserAggregateState.create(
    id: UUID,
    nickname: String,
    fullname: String,
    password: String,
): UserCreatedEvent {
    return UserCreatedEvent(
        userId = id,
        nickname = nickname,
        fullname = fullname,
        password = password,
        createdAt = System.currentTimeMillis()
    )
}