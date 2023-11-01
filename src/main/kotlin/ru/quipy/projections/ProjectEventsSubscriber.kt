package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.aggregate.project.ProjectAggregate
import ru.quipy.aggregate.project.events.*
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
                logger.info("Project created: id={}, title={}", event.projectId, event.title)
            }

            `when`(ProjectRenamedEvent::class) { event ->
                logger.info("Project renamed: id={}, title={}", event.projectId, event.title)
            }

            `when`(ProjectAddedMemberEvent::class) { event ->
                logger.info("User with id={} added to project with id={}", event.userId, event.projectId)
            }

            `when`(TaskAddedEvent::class) { event ->
                logger.info("Task with id={} created on project with id={}", event.taskId, event.projectId)
            }

            `when`(TaskNameUpdatedEvent::class) { event ->
                logger.info("Task renamed: id={}, title={}", event.taskId, event.title)
            }

            `when`(TaskAddedExecutorEvent::class) { event ->
                logger.info("Executor with id={} assigned to task with id={}", event.userId, event.taskId)
            }

            `when`(TaskStatusChangedEvent::class) { event ->
                logger.info("Task status changed: id={}, taskStatusId={} ", event.taskId, event.taskStatusId)
            }
        }
    }
}