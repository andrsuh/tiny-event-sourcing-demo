package ru.quipy.dtos.project

import java.util.UUID

data class CreateProjectDto(
    val name: String,
    val creatorId: UUID
)
