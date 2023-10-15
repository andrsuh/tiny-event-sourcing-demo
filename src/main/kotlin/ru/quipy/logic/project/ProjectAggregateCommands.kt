package ru.quipy.logic

import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.UserInvitedEvent
import ru.quipy.api.project.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.createStatus(name: String): StatusCreatedEvent {
    if (projectStatuses.values.any { it.name == name }) {
        throw IllegalArgumentException("Status already exists: $name")
    }
    return StatusCreatedEvent(projectId = this.getId(), statusId = UUID.randomUUID(), statusName = name)
}

fun ProjectAggregateState.inviteUser(id: UUID): UserInvitedEvent {
    if (projectUsers.values.any { it.userId == id }) {
        throw IllegalArgumentException("User already exists: $id")
    }
    return UserInvitedEvent(projectId = this.getId(), userId = id)
}

//
//fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
//    if (!projectTags.containsKey(tagId)) {
//        throw IllegalArgumentException("Tag doesn't exists: $tagId")
//    }
//
//    if (!tasks.containsKey(taskId)) {
//        throw IllegalArgumentException("Task doesn't exists: $taskId")
//    }
//
//    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
//}