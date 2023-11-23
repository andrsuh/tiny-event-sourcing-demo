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
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class UserProjectsExistenceCache (
    private val userProjectsCacheRepository: UserProjectsCacheRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager,
    private val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
){
    private val logger = LoggerFactory.getLogger(UserProjectsExistenceCache::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "projects::user-projects-cache") {
            `when`(ProjectCreatedEvent::class) { event ->
                createOrUpdateUserProjects(event.creatorId, event.projectId, event.title)
                logger.info("Update user projects cache, create project ${event.creatorId}-${event.projectId}")
            }
            `when`(UserAddedEvent::class) { event ->
                val project = projectEsService.getState(event.projectId)
                if (project != null) {
                    createOrUpdateUserProjects(event.userId, event.projectId, project.projectTitle)
                }
                logger.info("Update user projects cache, add user to project ${event.userId}-${event.projectId}")
            }
        }
    }
    private fun createOrUpdateUserProjects(userId: UUID, projectId: UUID, projectName: String) {
        var userProjects = userProjectsCacheRepository.findByIdOrNull(userId)
        if (userProjects == null)
            userProjects = UserProjects(userId)
        userProjects.projects[projectId] = Project(projectId, projectName)
        userProjectsCacheRepository.save(userProjects)
    }
}

@Document("user-projects-cache")
data class UserProjects(
    @Id
    var userId: UUID,
    val projects: MutableMap<UUID, Project> = mutableMapOf()
)

data class Project(
    val projectId: UUID,
    var name: String
)

@Repository
interface UserProjectsCacheRepository: MongoRepository<UserProjects, UUID>