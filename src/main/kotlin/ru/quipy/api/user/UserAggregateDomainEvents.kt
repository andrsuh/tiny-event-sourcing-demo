package ru.quipy.api.user

import ru.quipy.api.UserAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

// API
const val CREATED_NEW_USER = "CREATED_NEW_USER"

@DomainEvent(name = CREATED_NEW_USER)
data class UserCreatedEvent(
    val userId: UUID,
    val userName: String,
    val userNickName: String,
    val userPassword: String
) : Event<UserAggregate>(
    name = CREATED_NEW_USER,
    createdAt = System.currentTimeMillis(),
)