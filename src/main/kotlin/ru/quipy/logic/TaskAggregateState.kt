package ru.quipy.logic

import com.fasterxml.jackson.databind.BeanDescription
import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()

    lateinit var taskTitle: String
    lateinit var description: String
    lateinit var projectId: UUID
    lateinit var statusId: UUID
    var executorsId = mutableListOf<UUID>()

    override fun getId() = taskId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun TaskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        taskTitle = event.taskTitle
        description = event.description
        projectId = event.projectId
        statusId = event.statusId
    }
    
    @StateTransitionFunc
    fun statusSetApply(event: StatusSetEvent) {
        statusId = event.statusId
    }

    @StateTransitionFunc
    fun addExecutorApply(event: TaskExecutorAddedEvent) {
        executorsId.add(event.userId)
    }

    @StateTransitionFunc
    fun removeExecutorApply(event: TaskExecutorRemovedEvent) {
        executorsId.remove(event.userId)
    }
}