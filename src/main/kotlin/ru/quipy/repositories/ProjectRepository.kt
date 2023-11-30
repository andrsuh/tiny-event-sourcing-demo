package ru.quipy.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.subscribers.projections.views.ProjectViewDomain
import java.util.UUID

interface ProjectRepository : MongoRepository<ProjectViewDomain.Project, UUID> {
    fun findProjectById(
        projectId: UUID
    ): ProjectViewDomain.Project?
    fun findProjectsByParticipantIdsContaining(
        participantId: UUID
    ): List<ProjectViewDomain.Project>

    fun findProjectsByCreatorId(
        creatorId: UUID
    ): List<ProjectViewDomain.Project>

    fun findProjectsByCreatorIdAndParticipantIdsContaining(
        creatorId: UUID,
        participantId: UUID
    ): List<ProjectViewDomain.Project>
}