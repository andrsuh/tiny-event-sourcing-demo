package ru.quipy.entities.projectManagment

import java.util.UUID

data class Project(
    val id: UUID,
    val name: String,
    val participants: MutableList<UUID> = mutableListOf(),
    val tasks: MutableMap<UUID, Task> = mutableMapOf(),
    val statuses: MutableMap<UUID, Status>,
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis(),
    val creatorId: UUID,
)