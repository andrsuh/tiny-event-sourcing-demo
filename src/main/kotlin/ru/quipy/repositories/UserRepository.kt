package ru.quipy.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.subscribers.projections.views.UserViewDomain
import java.util.UUID

interface UserRepository : MongoRepository<UserViewDomain.User, UUID> {
}