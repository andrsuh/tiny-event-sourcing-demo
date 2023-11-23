package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.*
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class UserExistenceCache (
    private val userCacheRepository: UserCacheRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
){
    private val logger = LoggerFactory.getLogger(UserExistenceCache::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "users::user-cache") {
            `when`(UserCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    userCacheRepository.save(User(event.userId, event.userName, event.nickname))
                }
                logger.info("Update user cache, create user ${event.userId}")
            }
        }
    }
}

@Document("user-cache")
data class User(
    @Id
    var userId: UUID,
    val name: String,
    val nickname: String
)

@Repository
interface UserCacheRepository : MongoRepository<User, UUID>