package ru.quipy.logic.state

import ru.quipy.api.aggregate.UserAggregate
import ru.quipy.api.event.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var userName: String
    lateinit var userPassword: String
    var projects = mutableMapOf<UUID, UUID>()

    override fun getId() = userId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun userCreatedApply(event: UserCreatedEvent) {
        userId = event.userId
        userName = event.userName
        userPassword = event.userPassword
        updatedAt = createdAt
   }

    @StateTransitionFunc
    fun userNameChangedApply(event: UserNameChangedEvent) {
        userName = event.userName
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun userProjectAddedApply(event: UserProjectAddedEvent) {
        projects[event.projectId] = event.projectId
        updatedAt = createdAt
    }
}
