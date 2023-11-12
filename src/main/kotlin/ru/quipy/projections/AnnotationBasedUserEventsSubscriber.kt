package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.aggregate.UserAggregate
import ru.quipy.api.event.*
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class, subscriberName = "user-demo-subs-stream"
)
class AnnotationBasedUserEventsSubscriber {

    val logger: Logger = LoggerFactory.getLogger(AnnotationBasedUserEventsSubscriber::class.java)

    @SubscribeEvent
    fun userCreatedEventSubscriber(event: UserCreatedEvent) {
        logger.info("User {} created", event.userName)
    }

    @SubscribeEvent
    fun userNameChangedEventSubscriber(event: UserNameChangedEvent) {
        logger.info("User {} changed name to {}", event.userId, event.userName)
    }

    @SubscribeEvent
    fun userProjectAddedEventSubscriber(event: UserProjectAddedEvent) {
        logger.info("User {} added to project {}", event.userId, event.projectId)
    }
}