package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: String
    var projectStatuses = mutableMapOf<UUID, StatusEntity>()
    var projectUsers = mutableMapOf<UUID, UserEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
    }
    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        projectStatuses[event.statusId] = StatusEntity(event.statusId, event.statusName)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun userInvitedApply(event: UserInvitedEvent) {
        projectUsers[event.userId] = UserEntity(event.userId)
        updatedAt = createdAt
    }
}

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

data class UserEntity(
    val userId: UUID = UUID.randomUUID()
)