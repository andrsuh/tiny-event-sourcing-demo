package ru.quipy.logic.task

import ru.quipy.api.project.TaskCreatedEvent
import ru.quipy.api.project.TaskStatusChangedEvent
import ru.quipy.api.task.MemberAssignedToTaskEvent
import ru.quipy.api.task.TaskNameChangedEvent
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun TaskAggregateState.createTask(projectId: UUID, taskName: String): TaskCreatedEvent {
    return TaskCreatedEvent(
        taskId = UUID.randomUUID(),
        projectId = projectId,
        taskName = taskName
    )
}

fun TaskAggregateState.changeTaskName(userId: UUID, taskID: UUID, name: String): TaskNameChangedEvent {
    return TaskNameChangedEvent(
        userId = userId,
        taskId = taskID,
        taskName = name
    )
}

fun TaskAggregateState.assignMemberToTask(userId: UUID, taskID: UUID): MemberAssignedToTaskEvent {
    return MemberAssignedToTaskEvent(
        userId = userId,
        taskId = taskID
    )
}

fun TaskAggregateState.setTaskStatus(taskId: UUID, statusId: UUID): TaskStatusChangedEvent {
    if(statusID.equals(statusId)){
        throw IllegalStateException("Status: $statusId is already set to task: $taskId")
    }
    return TaskStatusChangedEvent(
        taskId = taskId,
        statusId = statusId
    )
}