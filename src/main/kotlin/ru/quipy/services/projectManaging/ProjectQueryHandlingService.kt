package ru.quipy.services.projectManaging

import ru.quipy.dtos.project.ProjectDto
import ru.quipy.dtos.project.ProjectInfoDto
import java.util.UUID

interface ProjectQueryHandlingService {
    fun findProjectById(id: UUID): ProjectDto?
    fun findProjectsByFilters(creatorId: UUID?, participantId: UUID?): List<ProjectInfoDto>
}