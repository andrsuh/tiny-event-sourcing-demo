package ru.quipy.entities.projectManagment

import ru.quipy.entities.common.User
import java.util.UUID

class Project(
    val id: UUID,
    var name: String,
    val participantIds: List<UUID>,
    val statusIds: List<UUID>,
    val taskIds: List<UUID>
)
{
}