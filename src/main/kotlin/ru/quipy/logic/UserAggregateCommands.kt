package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import java.util.*

fun UserAggregate.create(
    userId: UUID = UUID.randomUUID(),
    name: String,
    password: String,
    login: String
): UserCreatedEvent {
    if (name.length < 3) {
        throw IllegalArgumentException("Name is too small")
    }
    if (password.isBlank()) {
        throw IllegalArgumentException("Can't change password: empty provided")
    }
    if (password.length < 8) {
        throw IllegalArgumentException("Password is too weak")
    }

    return UserCreatedEvent(
        userId = userId,
        nickname = login,
        password = password,
        userName = name
    )
}