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

fun ProjectAggregateState.createStatus(name: String, color: String): StatusCreatedEvent {
    if (projectStatuses.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return StatusCreatedEvent(
        projectId = this.getId(),
        statusId = UUID.randomUUID(),
        statusColor = color,
        statusName = name
    )
}

fun ProjectAggregateState.changeProjectName(name: String): ProjectNameChangedEvent {
    if (this.name == name) {
        error("This name already set")
    }
    return ProjectNameChangedEvent(projectId = this.getId(), projectName = name)
}

fun ProjectAggregateState.addUserToProject(userId: UUID): UserAddedToProjectEvent {
    if (this.users.contains(userId)) {
        error("This user is already added to project")
    }
    return UserAddedToProjectEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.addTaskToProject(taskId: UUID): TaskAddedEvent {
    if (this.tasks.contains(taskId)) {
        error("Task was already added to project")
    }
    return TaskAddedEvent(projectId = this.getId(), taskId = taskId)
}

fun ProjectAggregateState.deleteStatus(statusId: UUID): StatusDeletedEvent {
    if (this.projectStatuses.containsKey(statusId)) {
        error("Project doesn't contains this status")
    }
    return StatusDeletedEvent(projectId = this.getId(), statusId = statusId)
}