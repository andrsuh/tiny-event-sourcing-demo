package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
// Service's business logic
class UserAggregateState : AggregateState<UUID, UserAggregate> {

    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var username: String
    lateinit var nickname: String
    lateinit var password: String
    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        createdAt = event.createdAt
        updatedAt = createdAt
        username = event.username
        nickname = event.nickname
        password = event.password
    }
}