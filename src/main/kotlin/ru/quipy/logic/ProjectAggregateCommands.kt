package ru.quipy.logic

import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TagChangeColorEvent
import ru.quipy.api.TagChangeNameEvent
import ru.quipy.api.TagCreatedEvent
import ru.quipy.api.UserAssignedToProjectEvent
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

// Create Project
fun ProjectAggregateState.create(id: UUID, title: String, creatorId: UUID): ProjectCreatedEvent {
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


fun ProjectAggregateState.assignUserToProject(userId: UUID, username: String, nickname: String): UserAssignedToProjectEvent {
    if (projectMembers.containsKey(userId)) {
        throw IllegalArgumentException("User already exists: $userId")
    }

    return UserAssignedToProjectEvent(projectId = this.getId(), userId = userId, username=username, nickname=nickname)
}

// Change Status name
fun ProjectAggregateState.changeName(name: String, tagId: UUID): TagChangeNameEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return TagChangeNameEvent(this.getId(), tagId, name)
}

// Change Status color
fun ProjectAggregateState.changeColor(color: String, tagId: UUID): TagChangeColorEvent {
    if (projectTags.values.any { it.color == color }) {
        throw IllegalArgumentException("Tag already exists: $color")
    }
    return TagChangeColorEvent(this.getId(), tagId, color)
}