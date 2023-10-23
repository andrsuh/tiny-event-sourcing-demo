package ru.quipy.logic.state

import ru.quipy.api.aggregate.ProjectAggregate
import ru.quipy.api.event.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: UUID
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var members = mutableMapOf<UUID, UUID>()
    var projectStatuses = mutableMapOf<UUID, StatusEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
        members[creatorId] = creatorId
        projectStatuses[UUID.fromString("00000000-0000-0000-0000-000000000000")] = StatusEntity(UUID.fromString("00000000-0000-0000-0000-000000000000"), "CREATED", "default")
    }

    @StateTransitionFunc
    fun projectStatusAddedApply(event: ProjectStatusAddedEvent) {
        projectStatuses[event.statusId] = StatusEntity(event.statusId, event.statusName, event.statusColor)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectTaskCreatedApply(event: ProjectTaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, UUID.fromString("00000000-0000-0000-0000-000000000000"), mutableSetOf())
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectTaskStatusChangedApply(event: ProjectTaskStatusChangedEvent) {
        tasks[event.taskId]?.statusAssigned = event.statusId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectTaskMemberAssignedApply(event: ProjectTaskMemberAssignedEvent) {
        tasks[event.taskId]?.membersAssigned?.add(event.memberId)
    }

    @StateTransitionFunc
    fun projectTaskTitleChangedApply(event: ProjectTaskTitleChangedEvent) {
        tasks[event.taskId]?.title = event.title
    }

    @StateTransitionFunc
    fun projectMemberAddedApply(event: ProjectMemberAddedEvent) {
        members[event.memberId] = event.memberId
    }

    @StateTransitionFunc
    fun projectTitleChangedApply(event: ProjectTitleChangedEvent) {
        projectTitle = event.title
    }

    @StateTransitionFunc
    fun projectStatusDeletedApply(event: ProjectStatusDeletedEvent) {
        projectStatuses.remove(event.statusId)
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var title: String,
    var statusAssigned: UUID,
    val membersAssigned: MutableSet<UUID>
)

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String
)
