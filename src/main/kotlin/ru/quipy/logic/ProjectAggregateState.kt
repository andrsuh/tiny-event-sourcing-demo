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
    lateinit var creatorId: UUID
    lateinit var updaterId: UUID
    lateinit var description: String
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var projectTags = mutableMapOf<UUID, TagEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        creatorId = event.creatorId
        title = event.title
        description = event.description
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectUpdateApply(event: ProjectInfoUpdatedEvent) {
        updaterId = event.userId
        title = event.title
        description = event.description
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectDeleteApply(event: ProjectDeletedEvent) {
        updaterId = event.userId
        title = ""
        description = ""
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName)
        updatedAt = createdAt
    }

//    @StateTransitionFunc
//    fun taskCreatedApply(event: TaskCreatedEvent) {
//        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, mutableSetOf())
//        updatedAt = createdAt
//    }
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

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}
