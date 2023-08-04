package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    lateinit var nickname: String
    lateinit var name: String
    lateinit var password: String

    override fun getId(): UUID = userId

    @StateTransitionFunc
    fun apply(event: UserCreatedEvent) {
        userId = event.userId
        nickname = event.nickname
        name = event.userName
        password = event.password
    }
}
