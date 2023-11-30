package ru.quipy.dtos.task

import ru.quipy.subscribers.projections.views.TaskViewDomain
import java.util.UUID

data class TaskInfoDto(
    val id: UUID,
    val name: String,
    val projectId: UUID,
)

fun TaskViewDomain.Task.toInfoDto(): TaskInfoDto {
    return TaskInfoDto(
        this.id.innerId,
        this.name,
        this.id.projectId,
    )
}