package ru.quipy.logic

import ru.quipy.api.*
import java.util.UUID

fun TaskAggregateState.createTask(projectId: UUID, taskId: UUID, taskName: String, statusId: UUID): TaskCreatedEvent{
    return  TaskCreatedEvent(projectId = projectId, taskId = taskId, statusId = statusId, taskName = taskName)
}

fun TaskAggregateState.changeTaskTitle(taskId: UUID, taskName: String) : TaskNameChangeEvent {
    if (name == taskName) {
        throw IllegalArgumentException("Task with this name already exists: $taskName")
    }
    return TaskNameChangeEvent(taskId = taskId, taskName = taskName)
}

fun TaskAggregateState.addExecutorToTask(userId: UUID, taskId: UUID) : AssignedExcutorToTaskEvent {
    if(executors.contains(userId)){
        throw IllegalArgumentException("User already exists: $userId")
    }
    return AssignedExcutorToTaskEvent(userId = userId, taskId = taskId)
}

fun TaskAggregateState.assignStatusToTask(projectId: UUID, taskId: UUID, statusId: UUID): StatusAssignedToTaskEvent {
    if(status == statusId){
        throw IllegalArgumentException("status already exists: $statusId")
    }
    var oldStatusID = status
    return StatusAssignedToTaskEvent(projectId = projectId, taskId = taskId, statusId = statusId, oldStatusId = oldStatusID)
}
