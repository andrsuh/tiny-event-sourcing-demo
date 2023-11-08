package ru.quipy.user.eda.projections

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.user.eda.api.UserAggregate
import javax.annotation.PostConstruct

@Service
class UserEventSubscriber {

    val logger = LoggerFactory.getLogger(UserEventSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "some-subscriber-name") {
            logger.debug("subscriber created for user")
        }
    }
}