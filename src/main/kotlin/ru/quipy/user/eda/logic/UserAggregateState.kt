package ru.quipy.user.eda.logic

import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.user.dto.UserModel
import ru.quipy.user.eda.api.UserAggregate
import ru.quipy.user.eda.api.UserCreatedEvent

import java.util.UUID

class UserAggregateState: AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    private lateinit var username: String
    private lateinit var realName: String
    private lateinit var password: String
    private var createdAt: Long = System.currentTimeMillis()

    override fun getId() = userId

    fun toModel() = UserModel(
        userId = this.userId,
        username = this.username,
        realName = this.realName,
        password = this.password
    )

    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        username = event.username
        realName = event.realName
        password = event.password
        createdAt = event.createdAt
    }
}
