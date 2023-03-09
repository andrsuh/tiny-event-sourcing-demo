package ru.quipy.logic

import org.springframework.stereotype.Service
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import java.util.*

@Service
class UserService(
    private val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
) {

    fun createUser(login: String, password: String): UserCreatedEvent {
        // TODO check of there is no already a user with the same login

        return userEsService.create {
            it.create(login = login, password = password)
        }
    }

    fun getUser(userId: UUID): UserAggregateState? = userEsService.getState(userId)

}