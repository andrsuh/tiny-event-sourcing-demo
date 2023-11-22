package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
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
                val bdUserProject = withContext(Dispatchers.IO) {
                    userProjectsCacheRepository.findById(event.creatorId)
                }
                var userProject = UserProjects(event.creatorId)
                if (!bdUserProject.isEmpty)
                    userProject = bdUserProject.get()
                userProject.projects[event.projectId] = Project(event.projectId, event.title)
                withContext(Dispatchers.IO) {
                    userProjectsCacheRepository.save(userProject)
                }
                logger.info("Update user projects cache, create projects ${event.creatorId}-${event.projectId}")
            }
            `when`(UserAddedEvent::class) { event ->
                val bdUserProject = withContext(Dispatchers.IO) {
                    userProjectsCacheRepository.findById(event.userId)
                }
                var userProject = UserProjects(event.userId)
                if (!bdUserProject.isEmpty)
                    userProject = bdUserProject.get()
                val project = projectEsService.getState(event.projectId)
                if (project != null) {
                    userProject.projects[event.projectId] = Project(event.projectId, project.projectTitle)
                }
                withContext(Dispatchers.IO) {
                    userProjectsCacheRepository.save(userProject)
                }
                logger.info("Update user projects cache, add user to project ${event.userId}-${event.projectId}")
            }
        }
    }
}

@Document("transactions-accounts-cache")
data class UserProjects(
    @Id
    var userId: UUID,
    val projects: MutableMap<UUID, Project> = mutableMapOf()
)

//data class ProjectMembers(
//    @Id
//    var projectId: UUID,
//    val users: MutableSet<UUID> = mutableSetOf()
//)

data class Project(
    val projectId: UUID,
    var name: String
)

@Repository
interface UserProjectsCacheRepository: MongoRepository<UserProjects, UUID>