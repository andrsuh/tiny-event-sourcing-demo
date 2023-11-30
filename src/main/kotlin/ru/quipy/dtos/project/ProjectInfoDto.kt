package ru.quipy.dtos.project

import ru.quipy.subscribers.projections.views.ProjectViewDomain
import java.util.UUID

data class ProjectInfoDto (
    val id: UUID,
    val name: String,
    val creatorId: UUID,
)

fun ProjectViewDomain.Project.toInfoDto() : ProjectInfoDto {
    return ProjectInfoDto(
        this.id,
        this.name,
        this.creatorId
    )
}