package ru.quipy.logic.user

import ru.quipy.aggregate.user.UserAggregate
import ru.quipy.aggregate.user.events.UserChangedNameEvent
import ru.quipy.aggregate.user.events.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var userName: String

    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        userName = event.userName
        createdAt = event.createdAt
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun userChangedNameApply(event: UserChangedNameEvent) {
        userName = event.userName
        updatedAt = event.createdAt
    }
}