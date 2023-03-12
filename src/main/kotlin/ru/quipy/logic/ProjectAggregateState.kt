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
    var statuses = mutableSetOf<StatusEntity>()
    var projectTags = mutableMapOf<UUID, TagEntity>()
    var members = mutableSetOf<UUID>()

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
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        statuses.add(StatusEntity(event.statusName, event.color))
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        statuses.remove(StatusEntity(event.statusName, event.color))
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun memberAddedApply(event: MemberAddedEvent) {
        members.add(event.memberId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun memberDeletedApply(event: MemberDeletedEvent) {
        members.remove(event.memberId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectRenamedApply(event: ProjectRenamedEvent) {
        projectTitle = event.title
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskAddedApply(event: TaskAddedEvent) {
        tasks[event.task.id] = event.task
        updatedAt = createdAt
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val tagsAssigned: MutableSet<UUID>
)

data class TagEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

data class StatusEntity(
        val name: String,
        val color: String
)

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}
