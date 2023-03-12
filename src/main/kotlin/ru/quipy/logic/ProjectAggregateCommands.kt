package ru.quipy.logic

import ru.quipy.api.*
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

//fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
//    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
//}

fun ProjectAggregateState.addTask(task: TaskEntity): TaskAddedEvent {
    return TaskAddedEvent(projectId = this.getId(), task = task)
}

fun ProjectAggregateState.createTag(name: String): TagCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name)
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

fun ProjectAggregateState.createStatus(name: String, color: String): StatusCreatedEvent {
    return StatusCreatedEvent(projectId = this.getId(), statusName = name, color = color)
}

fun ProjectAggregateState.deleteStatus(name: String, color: String): StatusDeletedEvent {
    if (!statuses.contains(StatusEntity(name, color))) {
        throw IllegalArgumentException("Status doesn't exists: $name")
    }
    return StatusDeletedEvent(projectId = this.getId(), statusName = name, color = color)
}

fun ProjectAggregateState.addMember(id: UUID): MemberAddedEvent {
    return MemberAddedEvent(projectId = this.getId(), memberId = id)
}

fun ProjectAggregateState.deleteMember(id: UUID): MemberDeletedEvent {
    if (!members.contains(id)) {
        throw IllegalArgumentException("Member doesn't exists: $id")
    }
    return MemberDeletedEvent(projectId = this.getId(), memberId = id)
}

fun ProjectAggregateState.renameProject(newTitle: String): ProjectRenamedEvent {
    return ProjectRenamedEvent(projectId = this.getId(), title = newTitle)
}