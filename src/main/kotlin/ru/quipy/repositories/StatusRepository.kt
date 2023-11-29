package ru.quipy.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.subscribers.projections.views.StatusViewDomain

interface StatusRepository : MongoRepository<StatusViewDomain.Status, StatusViewDomain.StatusId> {
}