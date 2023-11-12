package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.aggregate.UserAggregate
import ru.quipy.api.event.UserCreatedEvent
import ru.quipy.api.event.UserNameChangedEvent
import ru.quipy.api.event.UserProjectAddedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.state.UserAggregateState
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class UserCache (
    private val userAccountCacheRepository: UserAccountCacheRepository,
    private val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    val logger: Logger = LoggerFactory.getLogger(UserCache::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

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
                userAccountCacheRepository.deleteById(user.userId)
                userAccountCacheRepository.save(user)
                logger.info("User {} changed name to {}", event.userId, event.userName)
            }

            `when`(UserProjectAddedEvent::class) { event ->
                logger.info("User {} added to project {}", event.userId, event.projectId)
            }
        }
    }
}

@Document("users-account-cache")
data class UserAccount(
    @Id
    val userId: UUID,
    var userName: String
)

@Repository
interface UserAccountCacheRepository: MongoRepository<UserAccount, UUID>