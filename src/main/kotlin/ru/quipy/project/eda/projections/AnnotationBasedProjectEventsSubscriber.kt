package ru.quipy.project.eda.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import ru.quipy.project.ProjectRepository
import ru.quipy.project.eda.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.user.eda.api.UserAggregate
import javax.annotation.PostConstruct

@Component
class UsersProjectEventsSubscriber(
    private val userProjectsRepository: ProjectRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager) {

    val logger: Logger = LoggerFactory.getLogger(UsersProjectEventsSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "some-subscriber-name") {
            `when`(ProjectCreatedEvent::class) { event ->
                logger.info("Project created: {}", event.projectName)
            }
            `when`(ProjectParticipantAddedEvent::class) { event ->
                logger.info("Participant {} added for project {}", event.participantId, event.projectId)
            }
        }
    }

}