package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.aggregate.user.UserAggregate
import ru.quipy.aggregate.user.events.UserChangedNameEvent
import ru.quipy.aggregate.user.events.UserCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Service
class UserEventsSubscriber {

    private val logger: Logger = LoggerFactory.getLogger(UserEventsSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "user-events-subscriber") {

            `when`(UserCreatedEvent::class) { e ->
                logger.info("User created: {}", e.userName)
            }

            `when`(UserChangedNameEvent::class) { e ->
                logger.info("Username changed: {} for user with userId={}", e.userName, e.userId)
            }
        }
    }
}