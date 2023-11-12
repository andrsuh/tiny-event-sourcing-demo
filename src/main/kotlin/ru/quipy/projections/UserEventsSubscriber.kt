package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import ru.quipy.api.aggregate.UserAggregate
import ru.quipy.api.event.UserCreatedEvent
import ru.quipy.api.event.UserNameChangedEvent
import ru.quipy.api.event.UserProjectAddedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.state.UserAggregateState
import ru.quipy.projections.repository.UserAccountCacheRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class UserEventsSubscriber (
    private val userAccountCacheRepository: UserAccountCacheRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {

    val logger: Logger = LoggerFactory.getLogger(UserEventsSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "users::account-cache") {
            `when`(UserCreatedEvent::class) { event ->
                userAccountCacheRepository.save(UserAccount(event.userId, event.userName))
                logger.info("User {} created", event.userName)
            }

            `when`(UserNameChangedEvent::class) { event ->
                val user = userAccountCacheRepository.findById(event.userId).get()
                user.userName = event.userName
                logger.info("User {} changed name to {}", event.userId, event.userName)
            }
        }
    }

    @Document("users-account-cache")
    data class UserAccount(
        @Id
        val userId: UUID,
        var userName: String
    )
}