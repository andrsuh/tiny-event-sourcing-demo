package ru.quipy.aggregate.user.events

import ru.quipy.aggregate.user.UserAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
const val USER_NAME_CHANGED_EVENT = "USER_NAME_CHANGED_EVENT"

@DomainEvent(USER_CREATED_EVENT)
class UserCreatedEvent(
    val userId: UUID,
    val userName: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<UserAggregate>(
    name = USER_CREATED_EVENT,
    createdAt = createdAt
)

@DomainEvent(USER_NAME_CHANGED_EVENT)
class UserChangedNameEvent(
    val userId: UUID,
    val userName: String,
    createdAt: Long = System.currentTimeMillis()
) : Event<UserAggregate>(
    name = USER_NAME_CHANGED_EVENT,
    createdAt = createdAt
)