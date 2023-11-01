package ru.quipy.logic.project

import ru.quipy.aggregate.project.events.TaskStatusCreatedEvent
import ru.quipy.aggregate.project.events.TaskStatusDeletedEvent
import ru.quipy.logic.UserAccessConstraintChecker.Companion.canAccess
import java.util.*


fun ProjectAggregateState.createTaskStatus(callerId: UUID, title: String, colorRgb: Int): TaskStatusCreatedEvent {
    canAccess(this, callerId)
    return TaskStatusCreatedEvent(UUID.randomUUID(), getId(), title, colorRgb)
}

fun ProjectAggregateState.deleteTaskStatus(callerId: UUID, taskStatusId: UUID): TaskStatusDeletedEvent {
    canAccess(this, callerId)

    if (hasTasksWithStatus(taskStatusId)) {
        throw IllegalArgumentException("Status with id=$taskStatusId cannot be deleted since it has associated tasks")
    }

    return TaskStatusDeletedEvent(taskStatusId, getId())
}

private fun ProjectAggregateState.hasTasksWithStatus(taskStatusId: UUID): Boolean {
    for (task in tasks) {
        if (task.value.taskStatusId == taskStatusId) {
            return true;
        }
    }

    return false;
}