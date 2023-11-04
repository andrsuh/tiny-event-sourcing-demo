package ru.quipy.dtos.projectManagment.status

import ru.quipy.dtos.projectManagment.task.TaskDtoWithoutStatus

data class StatusDto (
    private val info: StatusInfoDto,
    val tasks: List<TaskDtoWithoutStatus>
)