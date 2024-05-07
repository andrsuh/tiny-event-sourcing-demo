package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var title: String
    var assigneesIds = mutableSetOf<UUID>()
    lateinit var projectId: UUID
    lateinit var statusId: UUID

    override fun getId() = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        title = event.title
        projectId = event.projectId
        createdAt = event.createdAt
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskTitleChangedApply(event: TaskTitleChangedEvent) {
        taskId = event.taskId
        title = event.updatedTitle
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun participantAssignedToTaskApply(event: ParticipantAssignedToTaskEvent) {
        taskId = event.taskId
        assigneesIds.add(event.participantId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        taskId = event.taskId
        statusId = event.statusId
        updatedAt = event.createdAt
    }
}
