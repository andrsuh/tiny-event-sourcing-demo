package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectMemberAddedEvent
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class ProjectEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "some-meaningful-name") {

            `when`(ProjectCreatedEvent::class) { event ->
                logger.info("Project is created: {}", event.projectName)
            }

            `when`(ProjectMemberAddedEvent::class) { event ->
                logger.info("User {} is added to project {}", event.projectId, event.memberId)
            }

            `when`(TaskStatusCreatedEvent::class) { event ->
                logger.info("Task status is created: {}", event.statusName)
            }
        }
    }
}
