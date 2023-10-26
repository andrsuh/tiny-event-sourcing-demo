package ru.quipy.logic

import ru.quipy.api.*
import java.util.*
import ru.quipy.logic.Status.OPENED
import ru.quipy.logic.StatusColor.GREEN

fun ProjectAggregateState.create(id: UUID, title: String, createdBy: UUID): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        createdBy = UUID.randomUUID(),
    )
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
fun ProjectAggregateState.addParticipantToProject(projectId: UUID, userId: UUID): ParticipantAddedToProjectEvent {
    if (this.getId() != projectId) {
        throw IllegalArgumentException("Mismatching project ID")
    }

    if (participantsID.contains(userId)) {
        throw IllegalArgumentException("User already a participant")
    }

    return ParticipantAddedToProjectEvent(projectId, userId)
}

fun ProjectAggregateState.createTaskInProject(title: String): TaskCreatedEvent {
    val projectId = this.getId()
    if (this.getId() != projectId) {
        throw IllegalArgumentException("Mismatching project ID")
    }
    val status = createStatusInProject(projectId, GREEN, OPENED)

    return TaskCreatedEvent(UUID.randomUUID(), title, status.id)
}

fun ProjectAggregateState.renameTask(taskId: UUID, newName: String): TaskRenamedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exist: $taskId")
    }

    return TaskRenamedEvent(taskId, newName)
}

fun ProjectAggregateState.assignTaskToUser(taskId: UUID, userId: UUID): TaskAssignedToUserEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exist: $taskId")
    }

    if (!participantsID.contains(userId)) {
        throw IllegalArgumentException("User is not a participant: $userId")
    }

    return TaskAssignedToUserEvent(taskId, userId)
}

fun ProjectAggregateState.selfAssignTask(taskId: UUID, userId: UUID): TaskSelfAssignedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exist: $taskId")
    }

    if (!participantsID.contains(userId)) {
        throw IllegalArgumentException("User is not a participant: $userId")
    }

    return TaskSelfAssignedEvent(taskId, userId)
}

fun ProjectAggregateState.createStatusInProject(projectId: UUID, color: StatusColor, value: Status): StatusCreatedEvent {
    if (this.getId() != projectId) {
        throw IllegalArgumentException("Mismatching project ID")
    }

    return StatusCreatedEvent(projectId, UUID.randomUUID(), color, value)
}

fun ProjectAggregateState.assignStatusToTask(taskId: UUID, statusId: UUID): StatusAssignedToTaskEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exist: $taskId")
    }

    if (!projectStatuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exist: $statusId")
    }

    return StatusAssignedToTaskEvent(taskId, statusId)
}

fun ProjectAggregateState.changeTaskStatus(taskId: UUID, newStatusId: UUID): TaskStatusChangedEvent {
    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exist: $taskId")
    }

    if (!projectStatuses.containsKey(newStatusId)) {
        throw IllegalArgumentException("Status doesn't exist: $newStatusId")
    }

    return TaskStatusChangedEvent(taskId, newStatusId)
}

fun ProjectAggregateState.deleteStatus(statusId: UUID): StatusDeletedEvent {
    if (!projectStatuses.containsKey(statusId)) {
        throw IllegalArgumentException("Status doesn't exist: $statusId")
    }

    return StatusDeletedEvent(statusId)
}

fun ProjectAggregateState.deleteTask(taskId: UUID): TaskDeletedEvent {
    return TaskDeletedEvent(taskId)
}

fun ProjectAggregateState.deleteProject(projectId: UUID): ProjectDeletedEvent {
    if (this.getId() != projectId) {
        throw IllegalArgumentException("Mismatching project ID")
    }

    return ProjectDeletedEvent(projectId)
}
