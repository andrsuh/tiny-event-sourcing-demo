package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID

    lateinit var nickname: String
    lateinit var password: String
    lateinit var userName: String

    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        userName = event.userName
        nickname = event.nickname
        password = event.password
    }
}