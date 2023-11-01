package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.addToProject
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.UUID

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "demo-subs-stream"
)
class AnnotationBasedProjectEventsSubscriber(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
)
{

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedProjectEventsSubscriber::class.java)

    @SubscribeEvent
    fun projectCreatedSubscriber(event: ProjectCreatedEvent) {
        logger.info("New project: {}", event.title)
        userEsService.update(event.creatorId) {
            it.addToProject(event.creatorId, event.projectId)
        }
    }

}
