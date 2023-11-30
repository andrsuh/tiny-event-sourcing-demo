package ru.quipy.subscribers.projections.handlers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.aggregates.userManagment.UserAggregate
import ru.quipy.events.userManagment.user.UserCreatedEvent
import ru.quipy.repositories.UserRepository
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.subscribers.projections.views.UserViewDomain

@Service
@AggregateSubscriber(
    aggregateClass = UserAggregate::class, subscriberName = "projection-user-event-subscriber"
)
class ProjectionUserEventsSubscriber(
    @Autowired
    private val userRepository: UserRepository
) {
    val logger: Logger = LoggerFactory.getLogger(ProjectionUserEventsSubscriber::class.java)

    @SubscribeEvent
    fun taskCreatedSubscriber(event: UserCreatedEvent) {
        val userView = UserViewDomain.User(event.userId, event.userName, event.userNickname)
        userRepository.insert(userView)

        logger.info(
            "User view updated with id {}, nickname {}, name {}",
            event.userNickname,
            event.userName,
            event.userId,
        )
    }
}