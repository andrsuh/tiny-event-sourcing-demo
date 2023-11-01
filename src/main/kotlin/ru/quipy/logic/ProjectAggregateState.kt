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
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var users = mutableMapOf<UUID, UserEntity>()
    var projectStatuses = mutableMapOf<UUID, StatusEntity>()

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
    fun tagCreatedApply(event: StatusCreatedEvent) {
        projectStatuses[event.statusId] = StatusEntity(event.statusId, event.statusName)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun addUserToProject(event: UserAddedToProjectEvent) {
        users[event.userId] = UserEntity(event.userId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun changeName(event: ProjectNameChangedEvent) {
        projectTitle = event.projectName
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun deleteStatus(event: StatusDeletedEvent) {
        projectStatuses.remove(event.statusId)
        updatedAt = createdAt
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

data class UserEntity(
    val id: UUID
)
