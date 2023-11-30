package ru.quipy.dtos.task

import ru.quipy.dtos.status.StatusInfoDto
import ru.quipy.dtos.user.UserDto

data class TaskDto(
    val info: TaskInfoDto,
    val assignees: List<UserDto>,
    val status: StatusInfoDto,
)


