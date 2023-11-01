package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID

    lateinit var projectTitle: String
    lateinit var creatorId: UUID
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
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        require(projectId == event.projectId)
        projectStatuses[event.statusId] = StatusEntity(event.statusId, event.statusName, event.statusColor)
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        require(projectId == event.projectId)
        tasks.values.find { task -> task.status == event.statusId } ?: projectStatuses.remove(event.statusId)
    }

    @StateTransitionFunc
    fun statusAssignedApply(event: StatusAssignedToTaskEvent) {
        require(projectId == event.projectId)
        tasks[event.taskId]?.let { it.status = event.statusId }
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        require(projectId == event.projectId)
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, createdStatusEntity.id, mutableSetOf())
    }

    @StateTransitionFunc
    fun taskRenamedApply(event: TaskRenamedEvent) {
        require(projectId == event.projectId)
        tasks[event.taskId]?.let { it.name = event.newName }
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    }

    @StateTransitionFunc
    fun userAssignedApply(event: UserAssignedToTaskEvent) {
        require(projectId == event.projectId)
        tasks[event.taskId]?.assignedUsers?.add(event.userId)
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    }

}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var status: UUID,
    val assignedUsers: MutableSet<UUID>
)

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String
)

val createdStatusEntity = StatusEntity(UUID.randomUUID(), "CREATED", "#123456")
