package ru.quipy.logic.project

import ru.quipy.aggregate.project.ProjectAggregate
import ru.quipy.aggregate.project.events.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

const val DEFAULT_TASK_STATUS_TITLE = "Created"
const val DEFAULT_TASK_STATUS_COLOR_RGB = 1010100

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var title: String
    lateinit var creatorId: UUID

    var memberIds = mutableSetOf<UUID>()
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var taskStatuses = mutableMapOf<UUID, TaskStatusEntity>()

    override fun getId() = projectId


    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        taskStatuses[event.defaultStatusId] = TaskStatusEntity(
            event.defaultStatusId,
            DEFAULT_TASK_STATUS_TITLE,
            DEFAULT_TASK_STATUS_COLOR_RGB
        )
        projectId = event.projectId
        title = event.title
        creatorId = event.creatorId
        createdAt = event.createdAt
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun projectRenamedApply(event: ProjectRenamedEvent) {
        title = event.title
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun projectAddedMemberApply(event: ProjectAddedMemberEvent) {
        memberIds.add(event.userId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskAddedApply(event: TaskAddedEvent) {
        tasks[event.taskId] = TaskEntity(
            event.taskId,
            event.title,
            event.description,
            event.taskStatusId,
            null
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskNameUpdatedApply(event: TaskNameUpdatedEvent) {
        tasks.getValue(event.taskId).title = event.title
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskAddedExecutorApply(event: TaskAddedExecutorEvent) {
        tasks.getValue(event.taskId).assigneeId = event.userId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedEvent) {
        taskStatuses[event.taskStatusId] = TaskStatusEntity(
            event.taskStatusId,
            event.title,
            event.colorRgb
        )
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusDeletedApply(event: TaskStatusDeletedEvent) {
        taskStatuses.remove(event.taskStatusId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        tasks.getValue(event.taskId).taskStatusId = event.taskStatusId
        updatedAt = event.createdAt
    }

}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var title: String,
    var description: String,
    var taskStatusId: UUID,
    var assigneeId: UUID?
)

data class TaskStatusEntity(
    val taskStatusId: UUID = UUID.randomUUID(),
    val title: String,
    val colorRgb: Int
)