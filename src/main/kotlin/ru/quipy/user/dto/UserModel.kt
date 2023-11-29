package ru.quipy.user.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

data class UserModel(
        val userId: UUID,
        val username: String,
        val realName: String,
        @JsonIgnore
        val password: String) {
}