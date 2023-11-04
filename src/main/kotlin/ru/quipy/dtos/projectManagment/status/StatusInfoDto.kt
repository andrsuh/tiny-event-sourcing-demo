package ru.quipy.dtos.projectManagment.status

import ru.quipy.entities.projectManagment.Status
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

data class StatusInfoDto(
    val name: String,
    val color: String
)

fun ProjectAggregateState.getStatusInfoDto(statusId: UUID): StatusInfoDto {
    val status = this.project.statuses[statusId]
        ?: throw IllegalArgumentException("Project doesn't have status with id $statusId")

    return StatusInfoDto(
        status.name,
        status.color.toString()
    )
}

fun Status.toInfoDto(): StatusInfoDto {
    return StatusInfoDto(
        this.name,
        this.color.toString()
    )
}