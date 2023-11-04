package ru.quipy.dtos.projectManagment.task

import java.util.UUID

data class UpdateTaskDto(
    val name: String?,
    val statusId: UUID?
)