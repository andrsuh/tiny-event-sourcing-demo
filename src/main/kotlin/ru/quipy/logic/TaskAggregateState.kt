package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

// Service's business logic
class TaskAggregateState : AggregateState<UUID, TaskAggregate>{
    private lateinit var taskId: UUID
    lateinit var name: String
    lateinit var status: UUID
    lateinit var projectId: UUID

    var executors = mutableSetOf<UUID>()
    override fun getId() = taskId

    @StateTransitionFunc
    fun createTask(event: TaskCreatedEvent){
        taskId = event.taskId
        projectId = event.projectId
        name = event.taskName
        description = event.description
        status = event.statusId
    }

    @StateTransitionFunc
    fun changeTask(event: TaskNameChangeEvent){
        name = event.taskName
    }

    @StateTransitionFunc
    fun addExecutorToTask(event: ListExecutorsUpdatedEvent){
        executors.add(event.userId)
    }

    @StateTransitionFunc
    fun assignStatustoTask(event: AssignedStatusToTaskEvent){
        status = event.statusId
    }
}
