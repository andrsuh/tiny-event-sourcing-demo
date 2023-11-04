package ru.quipy.dtos.projectManagment.task

import ru.quipy.entities.projectManagment.Task
import java.util.UUID

data class TaskDtoWithoutStatus(
    val info: TaskInfoDto,
    val assignees: List<UUID>
)

fun Task.toDtoWithoutStatus(): TaskDtoWithoutStatus {
    return TaskDtoWithoutStatus(
        toInfoDto(),
        this.assigneeIds,
    )
}
