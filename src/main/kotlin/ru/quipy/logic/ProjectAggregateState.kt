package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*
import java.util.UUID

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    lateinit var projectTitle: String
    lateinit var creatorId: String
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var participants = mutableMapOf<UUID, UserEntity>()
    var statuses = mutableMapOf<UUID, StatusEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun createProject(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        val taskStatus = StatusEntityEntity(event.defaultStatusId, event.defaultStatusName, event.defaultStatusColor, 0)
        statuses[taskStatus.id] = taskStatus
    }

    @StateTransitionFunc
    fun addParticipantToProject(event: AddParticipantToProjectEvent){
        participants[event.userId] = UserEntity(event.userId)
    }

    @StateTransitionFunc
    fun createStatus(event: StatusCreatedEvent) {
        statuses[event.statusId] = StatusEntity(event.statusId, event.statusName, event.color, 0)
    }

    @StateTransitionFunc
    fun changeStatus(event: ChangeStatusEvent){
        val taskquantity = statuses[event.tagId]?.taskquantity
        statuses[event.statusId] = StatusEntity(event.statusId, event.statusName, color, taskquantity)
    }

    @StateTransitionFunc
    fun deleteStatus(event: DeleteStatusEvent){
        statuses.remove(event.statusId)
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val statusId: UUID
)

data class UserEntity(
    val id: UUID
)

data class StatusEntity(
    val statusId: UUID,
    val statusName: String,
    val color: String,
    val taskquantity: Int?
){
    companion object {
        const val DEFAULT_STATUS = "CREATED"
    }
}

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
@StateTransitionFunc
fun ProjectAggregateState.assignStatusToTask(event: statusAssignedToTaskEvent) {
    val key = statuses.get(event.statusId)
        ?: throw IllegalArgumentException("No such status: ${event.statusId}")
    key.count = key.count?.plus(1)
    val key2 = statuses.get(event.oldStatusId)
        ?: throw IllegalArgumentException("No such staus: ${event.oldStatusId}")
    if(key2.name != DEFAULT_TAG){
        key2.count = key2.count?.minus(1)
    }
}

