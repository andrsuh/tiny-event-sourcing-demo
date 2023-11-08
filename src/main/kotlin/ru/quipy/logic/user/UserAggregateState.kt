package ru.quipy.logic.user

import ru.quipy.api.user.UserAggregate
import ru.quipy.api.user.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var nickname: String
    lateinit var name: String

    override fun getId() = userId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        nickname = event.nickname
        name = event.userName
        updatedAt = createdAt
    }
}
