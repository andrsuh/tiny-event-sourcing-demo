package ru.quipy.dtos.status

import ru.quipy.dtos.task.TaskDtoWithoutStatus

data class StatusDto(
    val info: StatusInfoDto,
    val tasks: List<TaskDtoWithoutStatus>
)