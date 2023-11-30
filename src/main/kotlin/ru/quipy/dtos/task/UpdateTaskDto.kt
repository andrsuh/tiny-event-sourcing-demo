package ru.quipy.dtos.task

import java.util.UUID

data class UpdateTaskDto(
    val name: String?,
    val statusId: UUID?
)