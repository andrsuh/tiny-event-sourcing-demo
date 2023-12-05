package ru.quipy.projection.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.aggregate.user.UserAggregate
import ru.quipy.aggregate.user.events.UserCreatedEvent
import ru.quipy.projection.dto.UserDto
import ru.quipy.projection.repository.UserProjectionRepository
import ru.quipy.projection.view.UserView
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*

@Service
@AggregateSubscriber(aggregateClass = UserAggregate::class, subscriberName = "user-subscriber")
class UserService(
    val userProjectionRepository: UserProjectionRepository
) {

    val logger: Logger = LoggerFactory.getLogger(UserService::class.java)

    @SubscribeEvent
    fun userCreatedSubscriber(event: UserCreatedEvent) {
        logger.info("User created: {}", event.userName)
        userProjectionRepository.save(UserView.User(
            event.userId,
            event.userName,
            event.createdAt
        ))
    }

    fun findUserById(id: UUID): UserDto? {
        val user = userProjectionRepository.findById(id).get()
        return UserDto(user.id, user.userName)
    }
}