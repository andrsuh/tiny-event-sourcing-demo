package ru.quipy.logic

import ru.quipy.api.*
import java.util.UUID

fun TaskAggregateState.createTask(projectId: UUID,
                                  taskId: UUID,
                                  taskName: String,
                                  statusId: UUID): TaskCreatedEvent{
    return  TaskCreatedEvent(projectId = projectId, taskId = taskId, statusId = statusId, taskName = taskName)
}
fun TaskAggregateState.changeTaskTitle(taskId: UUID, taskName: String) : TaskNameChangeEvent {
    if (name == taskName) {
        throw IllegalArgumentException("Task with this name already exists: $taskName")
    }
    return TaskNameChangeEvent(taskId = taskId, taskName = taskName)
}

fun TaskAggregateState.addExecutorToTask(userId: UUID, taskId: UUID) : ListExecutorsUpdatedEvent {
    if(executors.contains(userId)){
        throw IllegalArgumentException("User already exists: $userId")
    }
    return ListExecutorsUpdatedEvent(userId = userId, taskId = taskId)
}

fun TaskAggregateState.assignedStatusToTask(projectId: UUID, taskId: UUID, statusId: UUID): AssignedStatusToTaskEvent {
    if(status == statusId){
        throw IllegalArgumentException("status already exists: $statusId")
    }
    return AssignedStatusToTaskEvent(projectId = projectId, taskId = taskId, statusId = statusId)
}
