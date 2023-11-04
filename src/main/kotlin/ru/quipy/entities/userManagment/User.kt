package ru.quipy.entities.userManagment

import java.util.UUID

class User(
    val id : UUID,
    var nickname : String,
    var name : String
)
{
}