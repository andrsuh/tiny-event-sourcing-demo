package ru.quipy.logic

import org.springframework.util.StringUtils
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

fun ProjectAggregateState.addStatus(name: String, color: String): StatusCreatedEvent {
    require(StringUtils.hasText(name)) { "Name cannot be empty" }
    require(StringUtils.hasText(color)) { "Color cannot be empty" }
    require(Regex("^#(?:[0-9a-fA-F]{3}){1,2}\$").matches(color)) { "Color must be in #hex format" }
    return StatusCreatedEvent(
        projectId = this.getId(),
        statusId = UUID.randomUUID(),
        statusName = name,
        statusColor = color
    )
}

fun ProjectAggregateState.deleteStatus(projectId: UUID, statusId: UUID): StatusDeletedEvent {
    require(this.getId() == projectId)
    if (this.tasks.values.find { task -> task.status == statusId } == null) {
        projectStatuses.remove(statusId)
        return StatusDeletedEvent(
            projectId = projectId,
            statusId = statusId
        )
    } else
        throw IllegalArgumentException("Task with status id $statusId exists")
}

fun ProjectAggregateState.assignStatus(projectId: UUID, taskId: UUID, statusId: UUID): StatusAssignedToTaskEvent {
    require(this.getId() == projectId)
    require(!projectStatuses.containsKey(statusId)) { "Status doesn't exists: $statusId" }
    require(!tasks.containsKey(taskId)) { "Task doesn't exists: $taskId" }
    return StatusAssignedToTaskEvent(
        projectId = projectId,
        taskId = taskId,
        statusId = statusId
    )
}

fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
    require(StringUtils.hasText(name)) { "Name cannot be empty" }
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.renameTask(projectId: UUID, taskId: UUID, newName: String): TaskRenamedEvent {
    require(this.getId() == projectId)
    require(StringUtils.hasText(newName)) { "New name cannot be empty" }
    require(tasks.containsKey(taskId)) { "Task doesn't exists: $taskId" }
    return TaskRenamedEvent(
        projectId = projectId,
        taskId = taskId,
        newName = newName
    )
}

fun ProjectAggregateState.assignUser(projectId: UUID, taskId: UUID, userId: UUID): UserAssignedToTaskEvent {
    require(this.getId() == projectId)
    require(!tasks.containsKey(taskId)) { "Task doesn't exists: $taskId" }
    // TODO check if user exists in project
    return UserAssignedToTaskEvent(
        projectId = projectId,
        taskId = taskId,
        userId = userId
    )
}
