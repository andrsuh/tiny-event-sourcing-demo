package ru.quipy.dtos.user

import ru.quipy.subscribers.projections.views.UserViewDomain
import java.util.UUID

data class UserDto(
    val id: UUID,
    val nickName: String,
    val name: String
)

fun UserViewDomain.User.toDto(): UserDto {
    return UserDto(
        this.id,
        this.nickname,
        this.name
    )
}