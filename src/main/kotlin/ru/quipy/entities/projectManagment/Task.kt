package ru.quipy.entities.projectManagment

import ru.quipy.entities.common.User
import java.util.UUID

class Task(
    val id : UUID,
    var name : String,
    val projectId : UUID,
    var statusId : UUID,
    val assigneeIds : List<UUID>
)
{
}