package ru.quipy.logic.commands

import ru.quipy.api.event.*
import ru.quipy.logic.state.ProjectAggregateState
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creatorId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.addTask(name: String): ProjectTaskCreatedEvent {
    return ProjectTaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.projectStatusCreate(name: String, color: String): ProjectStatusAddedEvent {
    if (projectStatuses.values.any { it.name == name }) {
        throw IllegalArgumentException("Status already exists: $name")
    }
    return ProjectStatusAddedEvent(projectId = this.getId(), statusId = UUID.randomUUID(), statusName = name, statusColor = color)
}

fun ProjectAggregateState.projectTaskStatusChange(statusId: UUID, taskId: UUID): ProjectTaskStatusChangedEvent {
    if (!projectStatuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exists: $statusId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return ProjectTaskStatusChangedEvent(projectId = this.getId(), statusId = statusId, taskId = taskId)
}

fun ProjectAggregateState.projectTaskMemberAssign(memberId: UUID, taskId: UUID): ProjectTaskMemberAssignedEvent {
    if (!members.containsKey(memberId)) {
        throw IllegalArgumentException("Member doesn't exists: $memberId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return ProjectTaskMemberAssignedEvent(projectId = this.getId(), memberId = memberId, taskId = taskId)
}

fun ProjectAggregateState.projectTaskTitleChange(title: String, taskId: UUID): ProjectTaskTitleChangedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return ProjectTaskTitleChangedEvent(projectId = this.getId(), title = title, taskId = taskId)
}

fun ProjectAggregateState.projectMemberAdd(memberId: UUID): ProjectMemberAddedEvent {
    if (tasks.containsKey(memberId)) {
        throw IllegalArgumentException("This member has already existed: $memberId")
    }

    return ProjectMemberAddedEvent(projectId = this.getId(), memberId = memberId)
}

fun ProjectAggregateState.projectTitleChange(title: String): ProjectTitleChangedEvent {
    return ProjectTitleChangedEvent(projectId = this.getId(), title = title)
}

fun ProjectAggregateState.projectStatusDelete(statusId: UUID): ProjectStatusDeletedEvent {
    if (!projectStatuses.values.any { it.id == statusId }) {
        throw IllegalArgumentException("Status doesn't exist: $statusId")
    }
    if (tasks.values.any { it.statusAssigned == statusId}) {
        throw IllegalArgumentException("Status is not empty: $statusId")
    }
    return ProjectStatusDeletedEvent(projectId = this.getId(), statusId = statusId)
}