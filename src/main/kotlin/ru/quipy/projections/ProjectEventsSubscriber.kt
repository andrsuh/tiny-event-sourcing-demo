package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class ProjectEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project-events-subscriber") {

            `when`(ProjectCreatedEvent::class) { event ->
                logger.info("Project created: {}", event.title)
            }

            `when`(ParticipantAddedEvent::class) { event ->
                logger.info("Participant {} added to project {}", event.participantId, event.projectId)
            }

            `when`(ProjectTitleUpdated::class) { event ->
                logger.info("Project {} update title to {}: ", event.projectId, event.updatedTitle)
            }

            `when`(StatusCreatedEvent::class) { event ->
                logger.info("Project {} create status {}: ", event.projectId, event.statusId)
            }

            `when`(StatusDeletedEvent::class) { event ->
                logger.info("Project {} delete status {}: ", event.projectId, event.statusId)
            }
        }
    }
}
