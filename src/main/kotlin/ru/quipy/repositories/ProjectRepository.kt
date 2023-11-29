package ru.quipy.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.subscribers.projections.views.ProjectViewDomain
import java.util.UUID

interface ProjectRepository : MongoRepository<ProjectViewDomain.Project, UUID> {
}