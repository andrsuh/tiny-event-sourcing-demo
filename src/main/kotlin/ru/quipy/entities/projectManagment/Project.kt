package ru.quipy.entities.projectManagment

import ru.quipy.entities.common.User
import java.util.UUID

class Project(
    val id: UUID,
    var name: String,
    val participants: List<User>,
    val statuses: List<Status>,
    val tasks: List<Task>
)
{
}