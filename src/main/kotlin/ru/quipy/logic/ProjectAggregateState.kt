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
    lateinit var creatorId: UUID
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var projectTags = mutableMapOf<UUID, TagEntity>()
    var projectMembers = mutableListOf<UUID>()

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
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName, event.tagColor)
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun tagDeletedApply(event: TagDeletedEvent) {
        projectTags.remove(event.tagId)
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, mutableSetOf())
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun taskRenamedApply(event: TaskRenamedEvent) {
        tasks[event.taskId]?.name = event.taskName
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun tagAssignedToTaskApply(event: TagAssignedToTaskEvent) {
        tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
        tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun userAddedApply(event: UserAddedEvent) {
        projectMembers.add(event.userId)
        updatedAt = System.currentTimeMillis()
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    val tagsAssigned: MutableSet<UUID>
)

data class TagEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String
)
//
//data class MemberEntity(
//    val id: UUID = UUID.randomUUID(),
//    val name: String,
//    val nickname: String
//)
