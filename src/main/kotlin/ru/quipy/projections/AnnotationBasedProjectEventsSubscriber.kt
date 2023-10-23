package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.aggregate.ProjectAggregate
import ru.quipy.api.event.*
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "demo-subs-stream"
)
class AnnotationBasedProjectEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedProjectEventsSubscriber::class.java)

    @SubscribeEvent
    fun projectTaskMemberAssignedEventSubscriber(event: ProjectTaskMemberAssignedEvent) {
        logger.info("User {} assigned to task {}: ", event.memberId, event.taskId)
    }

    @SubscribeEvent
    fun projectTaskTitleChangedEventSubscriber(event: ProjectTaskTitleChangedEvent) {
        logger.info("Task {} changed title to {}: ", event.taskId, event.title)
    }

    @SubscribeEvent
    fun projectCreatedEventSubscriber(event: ProjectCreatedEvent) {
        logger.info("Task created: {} by user {}", event.title, event.creatorId)
    }

    @SubscribeEvent
    fun projectTaskCreatedEventSubscriber(event: ProjectTaskCreatedEvent) {
        logger.info("Task created: {}", event.taskName)
    }

    @SubscribeEvent
    fun projectStatusAddedEventSubscriber(event: ProjectStatusAddedEvent) {
        logger.info("Status created: {} with color {}", event.statusName, event.statusColor)
    }

    @SubscribeEvent
    fun projectTaskStatusChangedEventSubscriber(event: ProjectTaskStatusChangedEvent) {
        logger.info("Status {} assigned to task {}: ", event.statusId, event.taskId)
    }

    @SubscribeEvent
    fun projectMemberAddedEventSubscriber(event: ProjectMemberAddedEvent) {
        logger.info("User {} added to project {}: ", event.memberId, event.projectId)
    }

    @SubscribeEvent
    fun projectTitleChangedEventSubscriber(event: ProjectTitleChangedEvent) {
        logger.info("Project {} changed title to {}: ", event.projectId, event.title)
    }

    @SubscribeEvent
    fun projectStatusDeletedEventSubscriber(event: ProjectStatusDeletedEvent) {
        logger.info("Status {} deleted from project {}: ", event.statusId, event.projectId)
    }
}