package ru.quipy.logic

import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

// Create Project
fun ProjectAggregateState.create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}


// Create Status
fun ProjectAggregateState.createTag(name: String, color: String): TagCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name, color = color)
}

// Add Status To Project
fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}

fun ProjectAggregateState.assignUserToProject(userId: UUID): UserAssignedToProjectEvent {
    if (projectMembers.containsKey(userId)) {
        throw IllegalArgumentException("User already exists: $userId")
    }

    return UserAssignedToProjectEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.assignUserToTask(id: UUID, userId: String, projectId: UUID): TaskExecutorChangedEvent {
    return TaskExecutorChangedEvent(
        taskId = id,
        projectId = projectId,
        userId = userId,
    )
}