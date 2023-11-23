package ru.quipy.projections

import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserAddedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectMembersExistenceCache (
    private val projectMembersCacheRepository: ProjectMembersCacheRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
){
    private val logger = LoggerFactory.getLogger(ProjectMembersExistenceCache::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "projects::project-members-cache") {
            `when`(ProjectCreatedEvent::class) { event ->
                createOrUpdateProjectMembers(event.projectId, event.creatorId)
                logger.info("Update project members cache, create project ${event.projectId}-${event.creatorId}")
            }
            `when`(UserAddedEvent::class) { event ->
                createOrUpdateProjectMembers(event.projectId, event.userId)
                logger.info("Update project members cache, add user to project ${event.projectId}-${event.userId}")
            }
        }
    }
    private fun createOrUpdateProjectMembers(projectId: UUID, userId: UUID) {
        var projectMembers = projectMembersCacheRepository.findByIdOrNull(projectId)
        if (projectMembers == null)
            projectMembers = ProjectMembers(projectId)
        projectMembers.users.add(userId)
        projectMembersCacheRepository.save(projectMembers)
    }
}

@Document("project-members-cache")
data class ProjectMembers(
    @Id
    var projectId: UUID,
    val users: MutableSet<UUID> = mutableSetOf()
)

@Repository
interface ProjectMembersCacheRepository: MongoRepository<ProjectMembers, UUID>