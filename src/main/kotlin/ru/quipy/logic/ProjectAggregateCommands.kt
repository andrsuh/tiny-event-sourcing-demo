package ru.quipy.logic

import ru.quipy.api.*
import java.util.*
import kotlin.collections.LinkedHashSet


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.createProject(projectId: UUID, name: String, creatorId: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = projectId,
        projectName = name,
        creatorId = creatorId,
        defaultStatusId = UUID.randomUUID(),
    )
}

fun ProjectAggregateState.addProjectMember(memberId: UUID): ProjectMemberAddedEvent {
    if (members.contains(memberId)) {
        throw UniqueConstraintViolation("User is already a member of the project")
    }
    return ProjectMemberAddedEvent(projectId = getId(), memberId = memberId)
}

fun ProjectAggregateState.createTaskStatus(name: String, color: Color): TaskStatusCreatedEvent {
    if (taskStatuses.values.any { it.name == name }) {
        throw UniqueConstraintViolation("Status with the same name already exists")
    }
    return TaskStatusCreatedEvent(projectId = getId(), statusId = UUID.randomUUID(), statusName = name, color = color)
}

fun ProjectAggregateState.deleteTaskStatus(statusId: UUID): TaskStatusDeletedEvent {
    if (!taskStatuses.containsKey(statusId)) {
        throw NoSuchEntity("Task status $statusId does not exist")
    }
    val status = taskStatuses.getValue(statusId)
    if (status.isDefault) {
        throw IllegalStateException("Task status $statusId is the default status and cannot be deleted")
    }
    return TaskStatusDeletedEvent(projectId = getId(), statusId = statusId)
}

fun ProjectAggregateState.setTaskStatusesOrder(statusesIds: LinkedHashSet<UUID>): TaskStatusesOrderSetEvent {
    statusesIds.forEach {
        if (!taskStatuses.containsKey(it)) {
            throw NoSuchEntity("Task status $it does not exist")
        }
    }
    if (taskStatuses.count() != statusesIds.count()) {
        throw IllegalStateException("IDs list must include all the statuses")
    }
    return TaskStatusesOrderSetEvent(projectId = getId(), statusesIds = statusesIds)
}
