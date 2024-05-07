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

    lateinit var title: String
    lateinit var creatorId: String
    var participantIds = mutableSetOf<UUID>()
    var projectStatuses = mutableMapOf<UUID, StatusEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        title = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun participantAddedApply(event: ParticipantAddedEvent) {
        projectId = event.projectId
        participantIds.add(event.participantId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun projectTitleUpdatedApply(event: ProjectTitleUpdated) {
        projectId = event.projectId
        title = event.updatedTitle
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusCreatedEventApply(event: StatusCreatedEvent) {
        projectId = event.projectId
        projectStatuses[event.statusId] = StatusEntity(event.statusId, event.title, event.color, false, 0)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusCreatedEventApply(event: StatusDeletedEvent) {
        projectId = event.projectId
        projectStatuses[event.statusId]?.isDeleted = true
        updatedAt = event.createdAt
    }

}

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val color: String,
    var isDeleted: Boolean,
    val numberOfTaskInStatus: Int
)
