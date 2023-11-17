package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.user.UserAggregate
import ru.quipy.api.user.UserCreatedEvent
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class UserProjection(
        private val userProjectionRepository: UserProjectionRepository
) {

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(UserAggregate::class, "UserAggregateSubscriberUProjection") {

            `when`(UserCreatedEvent::class) { event ->
                userProjectionRepository.save(User(event.userId, event.nickname, event.firstName, event.name))
            }
        }
    }
}

@Document("user-projection")
data class User(
        @Id
        val userId: UUID,
        val nickname: String,
        val firstName: String,
        val name: String
)

@Repository
interface UserProjectionRepository : MongoRepository<User, UUID>