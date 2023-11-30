package ru.quipy.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.subscribers.projections.views.StatusViewDomain
import java.util.UUID

interface StatusRepository : MongoRepository<StatusViewDomain.Status, StatusViewDomain.StatusId> {
    fun findStatusById_ProjectIdAndId_InnerId(projectId: UUID, innerId: UUID) : StatusViewDomain.Status?
    fun findStatusesById_ProjectId(projectId: UUID) : List<StatusViewDomain.Status>
}