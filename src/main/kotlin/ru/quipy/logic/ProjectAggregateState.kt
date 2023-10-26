package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var name: String
    lateinit var createdBy: UUID
    var participantsID = mutableSetOf<UUID>() //MutableSet<UUID> = mutableSetOf()
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var projectTags = mutableMapOf<UUID, TagEntity>()
    var projectStatuses = mutableMapOf<UUID, StatusEntity>()

    override fun getId() = projectId

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        name = event.title
        createdBy = event.createdBy
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
//        val currentUserId = UserAggregateState().getId()
        val currentUserId = UUID.randomUUID()

        tasks[event.taskId] = TaskEntity(
            event.taskId,
            event.taskName,
            mutableSetOf(),
            event.statusId,
            currentUserId,
            currentUserId
        )
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusCreatedEvent) {
        projectStatuses[event.statusId] = StatusEntity(
            event.statusId,
            event.statusColor,
            event.statusValue
        )
        updatedAt = createdAt
    }
}

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val color: StatusColor,
    val status: Status,
)

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    val tagsAssigned: MutableSet<UUID>,
    var taskStatusId: UUID,
    var assignedToID: UUID,
    val createdBy: UUID
)

data class TagEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

enum class StatusColor {
    GREEN,
    RED,
    YELLOW,
}

enum class Status {
    CLOSED,
    OPENED,
}

@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}

@StateTransitionFunc
fun ProjectAggregateState.participantAddedToProjectApply(event: ParticipantAddedToProjectEvent) {
    participantsID.add(event.userId)
    updatedAt = System.currentTimeMillis()
}

@StateTransitionFunc
fun ProjectAggregateState.taskRenamedApply(event: TaskRenamedEvent) {
    tasks[event.taskId] ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    tasks[event.taskId]?.name = event.newName
    updatedAt = System.currentTimeMillis()
}

@StateTransitionFunc
fun ProjectAggregateState.taskAssignedToUserApply(event: TaskAssignedToUserEvent) {
    tasks[event.taskId] ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    tasks[event.taskId]?.assignedToID = event.userId
    updatedAt = System.currentTimeMillis()
}

@StateTransitionFunc
fun ProjectAggregateState.taskSelfAssignedApply(event: TaskSelfAssignedEvent) {
    tasks[event.taskId] ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    tasks[event.taskId]?.assignedToID = event.userId
    updatedAt = System.currentTimeMillis()
}

@StateTransitionFunc
fun ProjectAggregateState.statusAssignedToTaskApply(event: StatusAssignedToTaskEvent) {
    tasks[event.taskId] ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    tasks[event.taskId]?.taskStatusId = event.statusId
    updatedAt = System.currentTimeMillis()
}

@StateTransitionFunc
fun ProjectAggregateState.taskStatusChangedApply(event: TaskStatusChangedEvent) {
    tasks[event.taskId] ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    tasks[event.taskId]?.taskStatusId = event.newStatusId
    updatedAt = System.currentTimeMillis()
}

@StateTransitionFunc
fun ProjectAggregateState.statusDeletedApply(event: StatusDeletedEvent) {
    if (tasks.values.any { it.taskStatusId == event.statusId }) {
        throw IllegalArgumentException("Cannot delete status which is assigned to tasks: ${event.statusId}")
    }
    projectStatuses.remove(event.statusId)
    updatedAt = System.currentTimeMillis()
}

@StateTransitionFunc
fun ProjectAggregateState.taskDeletedApply(event: TaskDeletedEvent) {
    val task = tasks[event.taskId] ?: throw IllegalArgumentException("No such task: ${event.taskId}")
//    val currentUserId = UserAggregateState().getId()
    val currentUserId = UUID.randomUUID()

    if (task.createdBy != currentUserId) {
        throw IllegalAccessException("User is not the creator of the task: ${event.taskId}")
    }
    tasks.remove(event.taskId)
    updatedAt = System.currentTimeMillis()
}

@StateTransitionFunc
fun ProjectAggregateState.projectDeletedApply(event: ProjectDeletedEvent) {
    //    val currentUserId = UserAggregateState().getId()
    val currentUserId = UUID.randomUUID()

    if (this.createdBy != currentUserId) {
        throw IllegalAccessException("User is not the creator of the project: ${this.getId()}")
    }
    updatedAt = System.currentTimeMillis()
}
