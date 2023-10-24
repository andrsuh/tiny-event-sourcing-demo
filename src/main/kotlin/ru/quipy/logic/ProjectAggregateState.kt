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

    private lateinit var name: String
    lateinit var description: String
    var participants = mutableListOf<UUID>()
    var projectStatuses = mutableMapOf<UUID, StatusEntity>()
    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        name = event.projectName
        description = event.description
        participants.add(element = event.creatorId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun participantAddApply(event: ParticipantAddedEvent) {
        participants.add(element = event.userId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun leaveProjectApply(event: LeaveProjectEvent) {
        val success = participants.remove(element = event.userId)
        if (!success) {
            throw Exception(message = "User not found!")
        }
        updatedAt = event.createdAt
    }
    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        projectStatuses[event.statusId] = StatusEntity(id=event.statusId, name = event.statusName, colour = event.colour)
        updatedAt = event.createdAt
    }
    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        projectStatuses.remove(event.statusId) ?: throw Exception(message = "Status not found!")
        updatedAt = event.createdAt
    }
}

data class StatusEntity(
    val id: UUID,
    val name: String,
    val colour: String
)
