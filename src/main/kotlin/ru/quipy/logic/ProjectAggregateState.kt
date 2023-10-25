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
    var projectStatuses = mutableMapOf<UUID, StatusEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        projectStatuses = mutableMapOf(Pair(createdStatusEntity.id, createdStatusEntity))
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        require(projectId == event.projectId)
        projectStatuses[event.statusId] = StatusEntity(event.statusId, event.statusName, event.statusColor)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        require(projectId == event.projectId)
        tasks.values.find { task -> task.status == event.statusId } ?: projectStatuses.remove(event.statusId)
    }

    @StateTransitionFunc
    fun statusAssignedApply(event: StatusAssignedToTaskEvent) {
        tasks[event.taskId]?.let { it.status = event.statusId }
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, createdStatusEntity.id)
        updatedAt = createdAt
    }

    //TaskRenamedEvent
    //UserAssignedToTaskEvent


}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var status: UUID
)

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String
)

val createdStatusEntity = StatusEntity(UUID.randomUUID(), "CREATED", "#123456")

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
