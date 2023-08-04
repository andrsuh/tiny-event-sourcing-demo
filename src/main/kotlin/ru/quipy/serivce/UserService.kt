package ru.quipy.serivce

import org.springframework.stereotype.Service
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.NoSuchEntity
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.createUser
import java.util.*

@Service
class UserService(val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>) {
    fun createUser(nickname: String, name: String, password: String): UserCreatedEvent {
        return userEsService.create {
            it.createUser(
                userId = UUID.randomUUID(),
                nickname = nickname,
                name = name,
                password = password,
            )
        }
    }

    fun getUserById(userId: UUID): UserAggregateState {
        return userEsService.getState(userId) ?: throw NoSuchEntity("User $userId does not exist")
    }
}
