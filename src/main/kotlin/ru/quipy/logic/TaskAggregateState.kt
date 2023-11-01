package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()

    lateinit var projectId: UUID
    lateinit var creatorId: UUID
    lateinit var updaterId: UUID
    lateinit var title: String
    lateinit var description: String
    var status: TaskStatus = TaskStatus.CREATED

    override fun getId() = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        projectId = event.projectId
        creatorId = event.creatorId
        title = event.title
        description = event.description
        createdAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskTitleUpdatedApply(event: TaskTitleUpdatedEvent) {
        taskId = event.taskId
        title = event.title
        updaterId = event.updaterId
        createdAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusUpdatedApply(event: TaskStatusUpdatedEvent) {
        taskId = event.taskId
        status = event.status
        updaterId = event.updaterId
        createdAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskDeletedApply(event: TaskDeletedEvent) {
        taskId = event.taskId
        updaterId = event.userId
        createdAt = event.createdAt
    }
}

enum class TaskStatus {
    CREATED, IN_PROGRESS, DONE
}