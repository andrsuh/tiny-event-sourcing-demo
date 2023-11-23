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
class ProjectMembersProjection (
    private val projectMembersRepository: ProjectMembersRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
){
    private val logger = LoggerFactory.getLogger(ProjectMembersProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "projects::project-members-projection") {
            `when`(ProjectCreatedEvent::class) { event ->
                createOrUpdateProjectMembers(event.projectId, event.creatorId)
                logger.info("Update project members projection, create project ${event.projectId}-${event.creatorId}")
            }
            `when`(UserAddedEvent::class) { event ->
                createOrUpdateProjectMembers(event.projectId, event.userId)
                logger.info("Update project members projection, add user to project ${event.projectId}-${event.userId}")
            }
        }
    }
    private fun createOrUpdateProjectMembers(projectId: UUID, userId: UUID) {
        var projectMembers = projectMembersRepository.findByIdOrNull(projectId)
        if (projectMembers == null)
            projectMembers = ProjectMembers(projectId)
        projectMembers.users.add(userId)
        projectMembersRepository.save(projectMembers)
    }
}

@Document("project-members-projection")
data class ProjectMembers(
    @Id
    var projectId: UUID,
    val users: MutableSet<UUID> = mutableSetOf()
)

@Repository
interface ProjectMembersRepository: MongoRepository<ProjectMembers, UUID>