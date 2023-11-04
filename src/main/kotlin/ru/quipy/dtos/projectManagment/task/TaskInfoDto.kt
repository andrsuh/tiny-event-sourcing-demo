package ru.quipy.dtos.projectManagment.task

import ru.quipy.entities.projectManagment.Task
import java.util.UUID

data class TaskInfoDto (
    val name: String,
    val projectId : UUID,
)

fun Task.toInfoDto() : TaskInfoDto {
    return TaskInfoDto(
        this.name,
        this.projectId,
    )
}