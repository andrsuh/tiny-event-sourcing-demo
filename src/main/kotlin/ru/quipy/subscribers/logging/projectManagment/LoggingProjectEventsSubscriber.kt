package ru.quipy.subscribers.logging.projectManagment

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.events.projectManagment.project.AssigneeAddedEvent
import ru.quipy.events.projectManagment.project.ParticipantAddedEvent
import ru.quipy.events.projectManagment.project.ProjectCreatedEvent
import ru.quipy.events.projectManagment.project.StatusAddedEvent
import ru.quipy.events.projectManagment.project.StatusRemovedEvent
import ru.quipy.events.projectManagment.project.TaskChangedEvent
import ru.quipy.events.projectManagment.project.TaskCreatedEvent
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.subscribers.projections.handlers.ProjectionProjectEventsSubscriber

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "logging-project-event-subscriber"
)
class LoggingProjectEventsSubscriber {
    val logger: Logger = LoggerFactory.getLogger(ProjectionProjectEventsSubscriber::class.java)

    @SubscribeEvent
    fun projectCreatedSubscriber(event: ProjectCreatedEvent) {
        logger.info(
            "Project {} created with id {} by user with id {}",
            event.projectName,
            event.projectId,
            event.creatorId,
        )
    }

    @SubscribeEvent
    fun statusAddedSubscriber(event: StatusAddedEvent) {
        logger.info(
            "Status {} added with id {} to project with id {}",
            event.statusName,
            event.statusId,
            event.projectId,
        )
    }

    @SubscribeEvent
    fun statusRemovedSubscriber(event: StatusRemovedEvent) {
        logger.info(
            "Status with id {} removed from project with id {}",
            event.statusId,
            event.projectId,
        )
    }

    @SubscribeEvent
    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
        logger.info(
            "Task {} added with id {} to project with id {}",
            event.taskName,
            event.taskId,
            event.projectId,
        )
    }

    @SubscribeEvent
    fun taskChangedSubscriber(event: TaskChangedEvent) {
        if (event.newTaskName != null) {
            logger.info(
                "Task with id {} in project with id {} renamed to {}",
                event.taskId,
                event.projectId,
                event.newTaskName,
            )
        }
        if (event.newStatusId != null) {
            logger.info(
                "Task with id {} in project with id {} changed to status with id {}",
                event.taskId,
                event.projectId,
                event.newStatusId,
            )
        }
    }

    @SubscribeEvent
    fun participantAddedSubscriber(event: ParticipantAddedEvent) {
        logger.info(
            "User with id {} added to project with id {}",
            event.userId,
            event.projectId,
        )
    }

    @SubscribeEvent
    fun assigneeAddedSubscriber(event: AssigneeAddedEvent) {
        logger.info(
            "User with id {} assigned to task with id {} in project with id {}",
            event.userId,
            event.taskId,
            event.projectId,
        )
    }
}