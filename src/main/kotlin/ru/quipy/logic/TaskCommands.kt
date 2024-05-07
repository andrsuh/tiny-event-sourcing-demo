package ru.quipy.logic

import ru.quipy.api.*
import java.util.*

fun TaskAggregateState.createTask(projectId: UUID, title: String): TaskCreatedEvent {
    return TaskCreatedEvent(UUID.randomUUID(), projectId, title)
}

fun TaskAggregateState.changeTitle(updatedTitle: String): TaskTitleChangedEvent {
    if (title == updatedTitle) {
        throw IllegalArgumentException("New title is not different")
    }
    return TaskTitleChangedEvent(getId(), updatedTitle)
}

fun TaskAggregateState.assignee(participantId: UUID): ParticipantAssignedToTaskEvent {
    if (assigneesIds.contains(participantId)) {
        throw IllegalArgumentException("Participant with id=$participantId is already assigned")
    }
    return ParticipantAssignedToTaskEvent(getId(), participantId)
}

fun TaskAggregateState.setTaskStatus(statusId: UUID): TaskStatusChangedEvent {
    if (this.statusId == statusId) {
        throw IllegalArgumentException("Status with id=$statusId is already set")
    }
    return TaskStatusChangedEvent(getId(), statusId)
}
