package ru.quipy.logic

import ru.quipy.api.*
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

fun ProjectAggregateState.addTask(id: UUID, name: String): TaskCreatedEvent {
    if (tasks.values.any { it.id == id }){
        throw IllegalArgumentException("Task already exists: $id")
    }
    return TaskCreatedEvent(projectId = this.getId(), taskId = id, taskName = name)
}

fun ProjectAggregateState.createTag(id: UUID, name: String, color: String): TagCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return TagCreatedEvent(projectId = this.getId(), tagId = id, tagName = name, tagColor = color)
}

fun ProjectAggregateState.deleteTag(tagId: UUID): TagDeletedEvent {
    if (tasks.values.any { it.tagsAssigned.contains(tagId) }){
        throw IllegalArgumentException("Tag $tagId assigned to task")
    }
    return TagDeletedEvent(projectId = this.getId(), tagId = tagId)
}
fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}

fun ProjectAggregateState.renameTask(taskId: UUID, taskName: String): TaskRenamedEvent {

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TaskRenamedEvent(projectId = this.getId(), taskId = taskId, taskName = taskName)
}

fun ProjectAggregateState.addUser(userId: UUID): UserAddedEvent {
    if (projectMembers.any { it == userId }) {
        throw IllegalArgumentException("User $userId already is member of project")
    }
    return UserAddedEvent(projectId = this.getId(), userId = userId)
}

fun ProjectAggregateState.assignUserToTask(taskId: UUID, userId: UUID): UserAssignedToTaskEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }
    if (tasks[taskId]?.usersAssigned?.contains(userId) == true) {
        throw IllegalArgumentException("User $userId already assigned to task: $taskId")
    }
    return UserAssignedToTaskEvent(projectId = this.getId(), taskId = taskId, userId = userId)
}