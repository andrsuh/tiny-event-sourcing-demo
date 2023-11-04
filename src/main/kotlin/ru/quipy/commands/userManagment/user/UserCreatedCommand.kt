package ru.quipy.commands.userManagment.user

import ru.quipy.events.userManagment.user.UserCreatedEvent
import ru.quipy.states.userManagment.UserAggregateState
import java.util.UUID

fun UserAggregateState.create(id: UUID, nickname: String, name: String): UserCreatedEvent {
    return UserCreatedEvent(
        id,
        nickname,
        name,
    )
}