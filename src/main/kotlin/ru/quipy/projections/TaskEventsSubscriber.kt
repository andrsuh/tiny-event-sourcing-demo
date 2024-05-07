package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class TaskEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(TaskEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "task-events-subscriber") {

            `when`(TaskCreatedEvent::class) { event ->
                logger.info("Task created: {}", event.title)
            }

            `when`(TaskTitleChangedEvent::class) { event ->
                logger.info("Task {} update title to {}: ", event.taskId, event.updatedTitle)
            }

            `when`(ParticipantAssignedToTaskEvent::class) { event ->
                logger.info("Task {} assigned to {}: ", event.taskId, event.participantId)
            }

            `when`(TaskStatusChangedEvent::class) { event ->
                logger.info("Task {} set status {}: ", event.taskId, event.statusId)
            }
        }
    }
}
