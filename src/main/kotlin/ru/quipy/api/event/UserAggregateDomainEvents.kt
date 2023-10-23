package ru.quipy.api.event

import ru.quipy.api.aggregate.UserAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
const val USER_NAME_CHANGED_EVENT = "USER_NAME_CHANGED_EVENT"
const val USER_PROJECT_ADDED_EVENT = "USER_PROJECT_ADDED_EVENT"


// API
@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
    val userId: UUID,
    val userName: String,
    val userPassword: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_CREATED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = USER_NAME_CHANGED_EVENT)
class UserNameChangedEvent(
    val userId: UUID,
    val userName: String,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_NAME_CHANGED_EVENT,
    createdAt = createdAt,
)

@DomainEvent(name = USER_PROJECT_ADDED_EVENT)
class UserProjectAddedEvent(
    val userId: UUID,
    val projectId: UUID,
    createdAt: Long = System.currentTimeMillis(),
) : Event<UserAggregate>(
    name = USER_PROJECT_ADDED_EVENT,
    createdAt = createdAt,
)