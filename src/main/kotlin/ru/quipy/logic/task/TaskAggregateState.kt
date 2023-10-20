package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()
    lateinit var projectId: UUID
    lateinit var statusId: UUID

    lateinit var taskTitle: String
    lateinit var creatorId: String
    var executors = mutableMapOf<UUID, UserEntityTask>()

    override fun getId() = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        projectId = event.projectId
        taskTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
        taskId = event.taskId
    }

    @StateTransitionFunc
    fun taskTitleChangedApply(event: TaskTitleChangedEvent) {
        taskTitle = event.title
        updatedAt = createdAt
        taskId = event.taskId
    }

    @StateTransitionFunc
    fun userExecutorApply(event: TaskAddedExecutorEvent) {
        taskId = event.taskId
        executors[event.executorId] = UserEntityTask(event.executorId)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskAssignedApply(event: StatusAssignedToTaskEvent) {
        taskId = event.taskId
        updatedAt = event.createdAt
        statusId = event.statusId
    }
}

data class UserEntityTask(
        val userId: UUID
)
