package ru.quipy.logic.task

import ru.quipy.api.project.*
import ru.quipy.api.task.MemberAssignedToTaskEvent
import ru.quipy.api.task.TaskAggregate
import ru.quipy.api.task.TaskNameChangedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var taskName: String
    lateinit var projectId: UUID
    lateinit var statusID: UUID
    var assigneeIds = mutableListOf<UUID>()

    override fun getId() = taskId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        projectId = event.projectId
        taskName = event.taskName
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskNameChangedApply(event: TaskNameChangedEvent) {
        taskName = event.taskName
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun memberAssignedToTaskApply(event: MemberAssignedToTaskEvent) {
        assigneeIds.add(event.userId)
    }

    @StateTransitionFunc
    fun taskStatusSetApply(event: TaskStatusChangedEvent) {
        statusID = event.statusId;
    }
}
