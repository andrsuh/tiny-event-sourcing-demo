package ru.quipy.dtos.project

import ru.quipy.dtos.status.StatusDto
import ru.quipy.dtos.user.UserDto

data class ProjectDto(
    val info: ProjectInfoDto,
    val participantIds: List<UserDto>,
    val statuses: List<StatusDto>
)