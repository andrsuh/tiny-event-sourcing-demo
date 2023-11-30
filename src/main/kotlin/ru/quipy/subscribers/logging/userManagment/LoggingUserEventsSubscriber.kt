package ru.quipy.subscribers.logging.userManagment

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.aggregates.userManagment.UserAggregate
import ru.quipy.events.userManagment.user.UserCreatedEvent
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.subscribers.projections.handlers.ProjectionUserEventsSubscriber

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class, subscriberName = "logging-user-event-subscriber"
)
class LoggingUserEventsSubscriber {
    val logger: Logger = LoggerFactory.getLogger(ProjectionUserEventsSubscriber::class.java)

    @SubscribeEvent
    fun taskCreatedSubscriber(event: UserCreatedEvent) {
        logger.info(
            "User {} created with name {} and id {}",
            event.userNickname,
            event.userName,
            event.userId,
        )
    }
}