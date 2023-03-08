package ru.quipy.logic

import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectMemberAddedEvent
import ru.quipy.api.ProjectTitleChangedEvent
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskStatusRemovedEvent
import java.util.*

fun ProjectAggregateState.create(id: UUID = UUID.randomUUID(), title: String, creatorId: UUID): ProjectCreatedEvent =
    ProjectCreatedEvent(projectId = id, title = title, creatorId = creatorId)

fun ProjectAggregateState.changeTitle(id: UUID, title: String): ProjectTitleChangedEvent =
    ProjectTitleChangedEvent(projectId = id, title = title)

fun ProjectAggregateState.addProjectMember(projectId: UUID, memberId: UUID): ProjectMemberAddedEvent {
    if (memberId in memberIds) {
        throw IllegalStateException("Project member already in project: $memberId")
    }

    return ProjectMemberAddedEvent(projectId = projectId, memberId = memberId)
}

fun ProjectAggregateState.createTaskStatus(name: String): TaskStatusCreatedEvent {
    if (taskStatuses.values.any { it.name == name }) {
        throw IllegalStateException("Task status already exists: $name")
    }

    return TaskStatusCreatedEvent(projectId = this.getId(), taskStatusId = UUID.randomUUID(), taskStatusName = name)
}

fun ProjectAggregateState.removeTaskStatus(taskStatusId: UUID): TaskStatusRemovedEvent {
    if (!taskStatuses.containsKey(taskStatusId)) {
        throw IllegalStateException("No such task status: $taskStatusId")
    }

    return TaskStatusRemovedEvent(projectId = this.getId(), taskStatusId = taskStatusId)
}