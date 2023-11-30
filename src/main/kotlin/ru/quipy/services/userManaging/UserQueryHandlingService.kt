package ru.quipy.services.userManaging

import ru.quipy.dtos.user.UserDto
import java.util.UUID

interface UserQueryHandlingService {
    fun findUserById(id: UUID) : UserDto?
    fun findUsersByProjectId(projectId: UUID) : List<UserDto>
}