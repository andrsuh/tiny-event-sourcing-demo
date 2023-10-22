package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
const val USERNAME_CHANGED_EVENT = "USERNAME_CHANGED_EVENT"

@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
    val userId: UUID,
    val username: String,
    val password: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate> (
    name = USER_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(name = USERNAME_CHANGED_EVENT)
class UsernameChangedEvent(
    val userId: UUID,
    val username: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate> (
    name = USERNAME_CHANGED_EVENT,
    createdAt = createdAt
)
