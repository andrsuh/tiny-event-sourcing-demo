package ru.quipy.logic

import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.project.eda.api.ProjectAggregate
import ru.quipy.project.eda.api.ProjectCreatedEvent
import ru.quipy.project.eda.api.ProjectParticipantAddedEvent
import ru.quipy.project.eda.api.ProjectUpdatedEvent
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var name: String
    lateinit var owner: UUID
    var participants: MutableList<UUID> = mutableListOf()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        name = event.projectName
        owner = event.projectOwner
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun projectUpdatedApply(event: ProjectUpdatedEvent) {
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun projectParticipantAddedApply(event: ProjectParticipantAddedEvent) {
        participants.add(event.participantId)
        updatedAt = event.createdAt
    }
}