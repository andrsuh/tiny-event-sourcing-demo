package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {

    private lateinit var userId: UUID
    private lateinit var fullName: String
    private lateinit var nickname: String
    private lateinit var password: String
    private var userProjects = mutableSetOf<UUID>()

    override fun getId() = userId

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        fullName = event.fullName
        nickname = event.nickname
        password = event.password
    }

    @StateTransitionFunc
    fun userAddedToProjectApply(event: UserAddedToProjectEvent) {
        userId = event.userId
        userProjects.add(event.projectId)
    }
}