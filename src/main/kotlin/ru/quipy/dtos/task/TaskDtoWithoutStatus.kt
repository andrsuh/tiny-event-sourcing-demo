package ru.quipy.dtos.task

import ru.quipy.dtos.user.UserDto

data class TaskDtoWithoutStatus(
    val info: TaskInfoDto,
    val assignees: List<UserDto>
)
