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
class UserProjection (
    private val userRepository: UserRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
){
    private val logger = LoggerFactory.getLogger(UserProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "users::user-projection") {
            `when`(UserCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    userRepository.save(User(event.userId, event.userName, event.nickname))
                }
                logger.info("Update user projection, create user ${event.userId}")
            }
        }
    }
}

@Document("user-projection")
data class User(
    @Id
    var userId: UUID,
    val name: String,
    val nickname: String
)

@Repository
interface UserRepository : MongoRepository<User, UUID>