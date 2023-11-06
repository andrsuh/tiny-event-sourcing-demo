package ru.quipy.logic

import org.springframework.util.StringUtils
import ru.quipy.api.UserCreatedEvent
import java.util.*

fun UserAggregateState.create(id: UUID, fullName: String, nickname: String, password: String,): UserCreatedEvent {
    require(StringUtils.hasText(fullName)) { "Name cannot be empty" }
    require(StringUtils.hasText(nickname)) { "Nickname cannot be empty" }
    return UserCreatedEvent(id, fullName, nickname, password)
}

