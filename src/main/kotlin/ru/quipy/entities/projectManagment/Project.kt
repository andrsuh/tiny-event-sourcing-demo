package ru.quipy.entities.projectManagment

import java.util.UUID

class Project(
    val id: UUID,
    var name: String,
)
{
    val participantIds: List<UUID> = mutableListOf()
    val statusIds: List<UUID> = mutableListOf()
    val taskIds: List<UUID> = mutableListOf()
}