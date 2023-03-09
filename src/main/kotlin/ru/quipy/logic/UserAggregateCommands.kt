package ru.quipy.logic

import ru.quipy.api.UserCreatedEvent
import java.util.*

fun UserAggregateState.create(
    userId: UUID = UUID.randomUUID(),
    login: String,
    password: String,
): UserCreatedEvent = UserCreatedEvent(userId = userId, login = login, password = password)