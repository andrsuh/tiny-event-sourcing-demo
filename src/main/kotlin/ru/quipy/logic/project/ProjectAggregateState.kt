package ru.quipy.logic.project

import ru.quipy.api.project.*
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
    var memberIds = mutableListOf<UUID>()
    var projectStatuses = mutableListOf(
        StatusEntity(
            id = UUID.randomUUID(),
            projectId = this.projectId,
            name = "CREATED",
            color = "GREEN",
            isDeleted = false
        )
    )

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
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, mutableSetOf(), projectStatuses.stream().filter { st ->
            st.name.equals("CREATED") }.findFirst().get().id)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun memberAddedApply(event: MemberAddedEvent) {
        memberIds.add(event.userId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun projectNameChangedApply(event: ProjectNameChangedEvent) {
        this.projectTitle = event.projectName
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        val statusEntity = StatusEntity(
            projectId = event.projectId,
            name = event.statusName,
            color = event.color,
            isDeleted = false
        )
        projectStatuses.add(statusEntity);
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusDeletedApply(event: StatusDeletedEvent) {
        val status = projectStatuses.stream()
            .filter { st -> st.id.equals(event.statusId) }
            .findFirst().get()
        projectStatuses.remove(status)
        updatedAt = createdAt
    }

}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val tagsAssigned: MutableSet<UUID>,
    val statusId: UUID
)

data class TagEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val color: String,
    val projectId: UUID,
    val isDeleted: Boolean
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
