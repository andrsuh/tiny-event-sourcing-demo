package ru.quipy.user

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import ru.quipy.user.dto.UserModel
import ru.quipy.user.eda.api.UserAggregate

import java.util.*

const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
const val USER_DELETED_EVENT = "USER_DELETED_EVENT"

@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
        val user: UserModel,
        createdAt: Long = System.currentTimeMillis()
) : Event<UserAggregate>(
        name = USER_CREATED_EVENT,
        createdAt = createdAt
)

@DomainEvent(name = USER_DELETED_EVENT)
class UserDeletedEvent(
        val username: String,
        createdAt: Long = System.currentTimeMillis()
) : Event<UserAggregate>(
        name = USER_CREATED_EVENT,
        createdAt = createdAt
)
