package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: String
    lateinit var description: String
    var membersID = mutableListOf<UUID>()
    var statuses = mutableMapOf<UUID, StatusEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
    }
    
    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        statuses[event.statusId] = StatusEntity(event.statusId, event.statusName, event.statusColor)
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        statuses.remove(event.statusId)
    }
}

data class StatusEntity(
    val statusId: UUID = UUID.randomUUID(),
    val statusName: String,
    val statusColor: String
)
