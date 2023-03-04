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
    var taskStatuses = mutableMapOf<UUID, TaskStatusEntity>()

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
    fun taskStatusCreatedApply(event: TaskStatusCreatedEvent) {
        taskStatuses[event.taskStatusId] = TaskStatusEntity(event.taskStatusId, event.taskStatusName)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, mutableSetOf())
        updatedAt = createdAt
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val taskStatusesAssigned: MutableSet<UUID>
)

data class TaskStatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
@StateTransitionFunc
fun ProjectAggregateState.taskStatusAssignedApply(event: TaskStatusAssignedToTaskEvent) {
    tasks[event.taskId]?.taskStatusesAssigned?.add(event.taskStatusId)
        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}
