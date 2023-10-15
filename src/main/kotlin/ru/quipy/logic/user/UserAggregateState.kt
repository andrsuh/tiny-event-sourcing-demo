package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.api.user.UserCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*


class UserAggregateState : AggregateState<UUID, UserAggregate> {

    private lateinit var userId: UUID
    private lateinit var userName: String
    private lateinit var userNickName: String
    private lateinit var userPassword: String

    override fun getId(): UUID = userId

    @StateTransitionFunc
    fun createUser(event: UserCreatedEvent) {
        this.userId = event.userId
        this.userName = event.userName
        this.userNickName = event.userNickName
        this.userPassword = event.userPassword
    }

}


