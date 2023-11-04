package ru.quipy.dtos.projectManagment.task

import ru.quipy.dtos.projectManagment.status.StatusInfoDto
import ru.quipy.dtos.projectManagment.status.toInfoDto
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

data class TaskDto(
    val info: TaskInfoDto,
    val assignees: List<UUID>,
    val status : StatusInfoDto,
)

fun ProjectAggregateState.getTaskDto(taskId : UUID) : TaskDto {
    val task = this.tasks[taskId]
        ?: throw IllegalArgumentException("Project doesn't have task with id $taskId")
    val status = this.statuses[task.statusId]
        ?: throw IllegalArgumentException("Project doesn't have status with id ${task.statusId}")
    return TaskDto(
        task.toInfoDto(),
        task.assigneeIds,
        status.toInfoDto(),
    )
}


