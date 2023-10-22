package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.api.UserChangedNameEvent
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var nickname: String
    lateinit var name: String
    lateinit var password: String

    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        nickname = event.nickname
        name = event.firstname
        password = event.password
    }

    @StateTransitionFunc
    fun userChangedNameApply(event: UserChangedNameEvent) {
        name = event.newName
    }
}