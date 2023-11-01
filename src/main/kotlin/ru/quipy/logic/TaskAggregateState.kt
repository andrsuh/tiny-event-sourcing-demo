package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
// Service's business logic
class TaskAggregateState : AggregateState<UUID, TaskAggregate> {

    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var taskTitle: String
    lateinit var creatorId: UUID
    lateinit var projectId: UUID
    lateinit var tagId: UUID
    lateinit var executors: List<UUID>

    override fun getId() = taskId


    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        taskTitle = event.taskName
        creatorId = event.creatorId
        updatedAt = createdAt
        projectId = event.projectId
        tagId = event.tagId
        executors = event.executors
    }

    @StateTransitionFunc
    fun taskNameChangedApply(event: TaskNameChangedEvent) {
        taskTitle = event.name
        updatedAt = System.currentTimeMillis()
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        tagId = event.statusId;
        updatedAt = System.currentTimeMillis()
    }
}