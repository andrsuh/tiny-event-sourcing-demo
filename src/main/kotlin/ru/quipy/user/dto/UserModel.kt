package ru.quipy.user.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

data class UserModel(
        val id: String,
        val username: String,
        val name: String,
        @JsonIgnore
        val password: String) {
}