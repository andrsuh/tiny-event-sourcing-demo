package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


fun UserAggregateState.create(id: UUID, username: String, password: String): UserCreatedEvent {
    return UserCreatedEvent(
        userId = id,
        username = username,
        password = password
    )
}

fun UserAggregateState.changeName(id: UUID, nameUser: String): NameUserChangedEvent {
    return NameUserChangedEvent(
        userId = this.getId(),
        nameUser = nameUser)
}