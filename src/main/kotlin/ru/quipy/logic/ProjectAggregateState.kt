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
    var projectTags = mutableMapOf<UUID, TagEntity>()
    var projectMembers = mutableMapOf<UUID, ProjectMemberEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
        projectMembers = mutableMapOf()
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, null, executors = emptyList())
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun executorChangedApply(event: TaskExecutorChangedEvent) {
        tasks[event.taskId]?.executors?.plus(event.userId)
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun userAssignedToProjectEventApply(event: UserAssignedToProjectEvent) {
        projectMembers[event.userId] = ProjectMemberEntity(event.userId, event.username, event.nickname)
        updatedAt = System.currentTimeMillis()
    }
}

data class TaskEntity(
        val id: UUID = UUID.randomUUID(),
        val name: String,
        var tagsAssigned: UUID?,
        var executors: List<UUID>,
        )

data class TagEntity(
        val id: UUID = UUID.randomUUID(),
        val name: String
)

data class ProjectMemberEntity(
        val id: UUID = UUID.randomUUID(),
        val name: String,
        val nickname: String
)

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned = (event.tagId)
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}
