package ru.quipy.states.userManagment

import ru.quipy.aggregates.userManagment.UserAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.events.userManagment.user.UserCreatedEvent
import java.util.UUID

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var nickname: String
    lateinit var name: String

    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        nickname = event.userNickname
        name = event.userName

        createdAt = event.createdAt
        updatedAt = event.createdAt
    }
}