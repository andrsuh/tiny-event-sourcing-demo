package ru.quipy.api

import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.UUID

const val CHANGE_STATUS_USER_EVENT = "CHANGE_ROLE_USER_EVENT"
const val USER_CREATED_EVENT = "USER_CREATED_EVENT"
const val CHANGE_USER_NAME_EVENT = "CHANGE_USER_NAME_EVENT"


@DomainEvent(name = CHANGE_STATUS_USER_EVENT)
class ChangeRoleUserEvent(
    val userId: UUID,
    val login: String,
    val password: String,
    val newRole: String,
    val oldRole: String,
) : Event<UserAggregate> (
    name = CHANGE_STATUS_USER_EVENT,
)

@DomainEvent(name = USER_CREATED_EVENT)
class UserCreatedEvent(
    val userId: UUID,
    val login: String,
    val password: String,
    val role: String
) : Event<ProjectAggregate>(
    name = TASK_CREATED_EVENT,
)

@DomainEvent(name = CHANGE_USER_NAME_EVENT)
class ChangeUserNameEvent(
    val userId: UUID,
    val login: String,
) : Event<ProjectAggregate>(
    name = CHANGE_USER_NAME_EVENT,
)