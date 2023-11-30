package ru.quipy.task.eda.logic

import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.user.dto.TaskModel
import ru.quipy.task.eda.api.TaskAggregate
import ru.quipy.task.eda.api.TaskAssignedEvent
import ru.quipy.task.eda.api.TaskCreatedEvent
import ru.quipy.task.eda.api.TaskUpdatedEvent

import java.util.UUID

class TaskAggregateState: AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    private lateinit var taskName: String
    var createdAt: Long = System.currentTimeMillis()

    override fun getId() = taskId

    fun toModel() = TaskModel(
        taskId = this.taskId,
        taskName = this.taskName,
        createdAt = this.createdAt,
    )

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        taskName = event.taskName
        createdAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskUpdatedApply(event: TaskUpdatedEvent) {
        taskId = event.taskId
        taskName = event.newTaskName
        createdAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskAssignedApply(event: TaskAssignedEvent) {
        taskId = event.taskId
        createdAt = event.createdAt
    }
}
