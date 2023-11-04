package ru.quipy.dtos.projectManagment.project

import ru.quipy.dtos.projectManagment.status.StatusDto
import ru.quipy.dtos.projectManagment.status.toInfoDto
import ru.quipy.dtos.projectManagment.task.toDtoWithoutStatus
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

data class ProjectDto(
    val id: UUID,
    val name: String,
    val statuses: List<StatusDto>
)

fun ProjectAggregateState.toDto(): ProjectDto {
    return ProjectDto(
        this.getId(),
        this.project.name,
        this.project.statuses.values.map {
            StatusDto(
                it.toInfoDto(),
                this.project.tasks.values
                    .asSequence()
                    .filter { task ->
                        task.statusId == it.id
                    }
                    .map { task ->
                        task.toDtoWithoutStatus()
                    }
                    .toList()
            )
        }
    )
}