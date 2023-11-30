package ru.quipy.entities.userManagment

import java.util.UUID

data class User(
    val id: UUID,
    val nickname: String,
    var name: String,
)