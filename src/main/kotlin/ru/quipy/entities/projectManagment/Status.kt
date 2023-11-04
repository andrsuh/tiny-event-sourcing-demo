package ru.quipy.entities.projectManagment

import java.awt.Color
import java.util.UUID

data class Status(
    val id: UUID,
    val name: String,
    val color: Color,
)