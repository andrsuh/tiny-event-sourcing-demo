package ru.quipy.user

import ru.quipy.user.dto.UserModel

data class UserCreatedEvent(val user: UserModel)

data class UserDeletedEvent(val username: String)
