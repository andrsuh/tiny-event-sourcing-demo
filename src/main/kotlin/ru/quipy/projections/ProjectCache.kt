package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.aggregate.ProjectAggregate
import ru.quipy.api.aggregate.UserAggregate
import ru.quipy.api.event.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.commands.addProject
import ru.quipy.logic.state.UserAggregateState
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectCache (
    private val projectCacheRepository: ProjectCacheRepository,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    val logger: Logger = LoggerFactory.getLogger(ProjectCache::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "some-meaningful-name") {
            `when`(ProjectCreatedEvent::class) { event ->
                projectCacheRepository.save(Project(event.projectId, event.title))
                logger.info("Project created: {} by user {}", event.title, event.creatorId)
                userEsService.update(event.creatorId) {
                    it.addProject(event.projectId)
                }
            }

            `when`(ProjectTaskCreatedEvent::class) { event ->
                logger.info("Task created: {}", event.taskName)
            }

            `when`(ProjectStatusAddedEvent::class) { event ->
                logger.info("Status created: {} with color {}", event.statusName, event.statusColor)
            }

            `when`(ProjectTaskStatusChangedEvent::class) { event ->
                logger.info("Status {} assigned to task {}: ", event.statusId, event.taskId)
            }

            `when`(ProjectMemberAddedEvent::class) { event ->
                logger.info("User {} added to project {}: ", event.memberId, event.projectId)
            }

            `when`(ProjectTitleChangedEvent::class) { event ->
                val project = projectCacheRepository.findById(event.projectId).get()
                project.projectTitle = event.title
                projectCacheRepository.deleteById(project.projectId)
                projectCacheRepository.save(Project(event.projectId, event.title))
                logger.info("Project {} changed title to {}: ", event.projectId, event.title)
            }

            `when`(ProjectStatusDeletedEvent::class) { event ->
                logger.info("Status {} deleted from project {}: ", event.statusId, event.projectId)
            }

            `when`(ProjectTaskMemberAssignedEvent::class) { event ->
                logger.info("User {} assigned to task {}: ", event.memberId, event.taskId)
            }

            `when`(ProjectTaskTitleChangedEvent::class) { event ->
                logger.info("Task {} changed title to {}: ", event.taskId, event.title)
            }
        }
    }
}

@Document("users-account-cache")
data class Project(
    @Id
    val projectId: UUID,
    var projectTitle: String
)

@Repository
interface ProjectCacheRepository: MongoRepository<Project, UUID>
