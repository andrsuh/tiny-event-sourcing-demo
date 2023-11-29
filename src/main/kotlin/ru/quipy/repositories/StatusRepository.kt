package ru.quipy.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.subscribers.projections.views.StatusViewDomain
import java.util.UUID

interface StatusRepository : MongoRepository<StatusViewDomain.Status, UUID> {
}