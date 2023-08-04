package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    lateinit var projectId: UUID
    lateinit var name: String
    val assignees = mutableSetOf<UUID>()
    lateinit var statusId: UUID

    override fun getId(): UUID = taskId

    @StateTransitionFunc
    fun apply(event: TaskCreatedEvent) {
        taskId = event.taskId
        projectId = event.projectId
        name = event.taskName
        statusId = event.statusId
    }

    @StateTransitionFunc
    fun apply(event: TaskRenamedEvent) {
        name = event.newName
    }

    @StateTransitionFunc
    fun apply(event: TaskAssignedEvent) {
        assignees.add(event.assigneeId)
    }

    @StateTransitionFunc
    fun apply(event: TaskStatusSetEvent) {
        statusId = event.statusId
    }
}
