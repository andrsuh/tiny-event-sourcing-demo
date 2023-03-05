package ru.quipy.logic

import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectMemberAddedEvent
import ru.quipy.api.ProjectTitleChangedEvent
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskStatusRemovedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var title: String
    lateinit var creatorId: UUID
    val taskStatuses: MutableMap<UUID, TaskStatusEntity> = with(TaskStatusEntity.created()) {
        mutableMapOf(this.id to this)
    }
    val memberIds = mutableSetOf<UUID>()

    override fun getId(): UUID = projectId

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        title = event.title
        creatorId = event.creatorId
        memberIds.add(creatorId)
        createdAt = event.createdAt
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun projectTitleChangedApply(event: ProjectTitleChangedEvent) {
        title = event.title
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun projectMemberAddedApply(event: ProjectMemberAddedEvent) {
        memberIds.add(event.memberId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusCreatedApply(event: TaskStatusCreatedEvent) {
        taskStatuses.values.firstOrNull { it.name ==  event.taskStatusName}
            ?.let {
                throw IllegalStateException("Task status already exists: ${event.name}")
            }

        taskStatuses[event.taskStatusId] = TaskStatusEntity(event.taskStatusId, event.taskStatusName)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusRemovedApply(event: TaskStatusRemovedEvent) {
        taskStatuses.remove(event.taskStatusId)
            ?: throw IllegalStateException("No such task status: ${event.taskStatusId}")
        updatedAt = event.createdAt
    }
}

data class TaskStatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
) {
    companion object {
        const val DEFAULT_TASK_STATUS_NAME = "CREATED"

        fun created(): TaskStatusEntity = TaskStatusEntity(name = DEFAULT_TASK_STATUS_NAME)
    }
}