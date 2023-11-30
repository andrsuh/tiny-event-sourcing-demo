package ru.quipy.services.projectManaging

import ru.quipy.dtos.status.StatusDto
import ru.quipy.dtos.status.StatusInfoDto
import java.util.UUID

interface StatusQueryHandlingService {
    fun findStatusById(projectId: UUID, innerId: UUID): StatusDto?
    fun findStatusesByProjectId(projectId: UUID): List<StatusInfoDto>
}