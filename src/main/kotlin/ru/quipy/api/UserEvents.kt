package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"

@DomainEvent(USER_CREATED_EVENT)
class UserCreatedEvent(
    val userId: UUID,
    val nickname: String,
    val userName: String,
    val password: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<UserAggregate>(
    name = USER_CREATED_EVENT,
    createdAt = createdAt
)
