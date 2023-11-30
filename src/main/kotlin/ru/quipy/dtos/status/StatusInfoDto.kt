package ru.quipy.dtos.status

import ru.quipy.subscribers.projections.views.StatusViewDomain
import java.util.UUID

data class StatusInfoDto(
    val id: UUID,
    val projectId : UUID,
    val name: String,
    val color: String
)

fun StatusViewDomain.Status.toInfoDto(): StatusInfoDto {
    return StatusInfoDto(
        this.id.innerId,
        this.id.projectId,
        this.name,
        this.colorCode
    )
}