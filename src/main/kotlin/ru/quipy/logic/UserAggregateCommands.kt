package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent

fun UserAggregateState.createUser(id: UUID, userName: String, nickname : String, password : String): UserCreatedEvent {
    if ((userName.length > 255) || (nickname.length > 255) || (password.length > 255)) {
        throw IllegalArgumentException("User data should be less than 255 characters!")
    }

    return UserCreatedEvent(
            userId = id,
            userName = userName,
            nickname = nickname,
            password = password
    )
}

