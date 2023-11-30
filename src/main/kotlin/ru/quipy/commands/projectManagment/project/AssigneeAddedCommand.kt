package ru.quipy.commands.projectManagment.project

import ru.quipy.events.projectManagment.project.AssigneeAddedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

fun ProjectAggregateState.assignToTask(userId: UUID, taskId: UUID): AssigneeAddedEvent {
    if (!this.project.participants.contains(userId)) {
        throw IllegalArgumentException("Project doesn't have participant with id $userId")
    }
    if (!this.project.tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Project doesn't have task with id $taskId")
    }
    return AssigneeAddedEvent(
        this.getId(),
        taskId,
        userId,
    )
}