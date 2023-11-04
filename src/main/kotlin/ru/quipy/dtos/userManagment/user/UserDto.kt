package ru.quipy.dtos.userManagment.user

import ru.quipy.states.userManagment.UserAggregateState
import java.util.UUID

data class UserDto(
    val id: UUID,
    val nickName: String,
    val name: String
)

fun UserAggregateState.toDto(): UserDto {
    return UserDto(
        this.getId(),
        this.nickname,
        this.name
    )
}