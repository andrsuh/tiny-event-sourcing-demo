package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.createProject(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

//fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
//    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
//}

fun ProjectAggregateState.addParticipantToProject(userId: UUID): AddParticipantToProjectEvent {
    if(participants.containsKey(userId)){
        throw IllegalArgumentException("User with id: $userId already exists in project")
    }
    return AddParticipantToProjectEvent(projectId = this.getId(), participantId = userId)
}

fun ProjectAggregateState.assignStatusToTask(statusId: UUID, taskId: UUID, oldStatusId: UUID): StatusAssignedToTaskEvent {
    if (!statuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }

    return StatusAssignedToTaskEvent(projectId = this.getId(), statusId = statusId, taskId = taskId, oldStatusId = oldStatusId)
}

fun ProjectAggregateState.createStatus(name: String, color: String): StatusCreatedEvent {
    if (statuses.values.any { it.statusName == name }) {
        throw IllegalArgumentException("Status already exists: $name")
    }
    return StatusCreatedEvent(projectId = this.getId(), statusId = UUID.randomUUID(), statusName = name, color = color, 0)
}

fun ProjectAggregateState.deleteStatus(statusId: UUID): DeleteStatusEvent{
    if(!statuses.containsKey(statusId)){
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }
    val status = statuses.get(statusId)
    if(status?.taskQuantity!! > 0){
        throw IllegalArgumentException("Status have tasks:")
    }
    return DeleteStatusEvent(projectId = this.getId(), statusId = statusId)
}

fun ProjectAggregateState.changeStatus(statusId: UUID, statusName: String): ChangeStatusEvent{
    if(!statuses.containsKey(statusId)){
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }
    if(statuses.values.any { it.statusName == statusName }){
        throw IllegalArgumentException("Stauts already exists: $statusName")
    }
    val status_ = statuses[statusId]
    return ChangeStatusEvent(projectId = this.getId(), statusId = statusId, statusName = statusName)
}
