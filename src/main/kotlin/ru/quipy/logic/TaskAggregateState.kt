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

    override fun getId() = taskId


    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        taskTitle = event.taskName
        creatorId = event.creatorId
        updatedAt = createdAt
        projectId = event.projectId
        tagId = event.tagId
    }

    @StateTransitionFunc
    fun taskNameChangedApply(event: TaskNameChangedEvent) {
//        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, null)
//        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
//        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, null)
//        updatedAt = createdAt
    }
}