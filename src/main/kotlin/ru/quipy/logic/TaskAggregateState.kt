package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState: AggregateState<UUID, TaskAggregate> {
    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var taskName: String
    lateinit var status: StatusEntity
    var executors = mutableSetOf<String>()

    override fun getId() = taskId

//    @StateTransitionFunc
//    fun statusCreatedApply(event: StatusCreatedEvent) {
//        status = StatusEntity(event.statusId, event.statusName, event.color)
//        updatedAt = createdAt
//    }

    @StateTransitionFunc
    fun statusSetApply(event: StatusSetEvent) {
        status = event.status
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun executorAddedApply(event: ExecutorAddedEvent) {
        executors.add(event.executorName)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskRenamedApply(event: TaskRenamedEvent) {
        taskName = event.name
        updatedAt = createdAt
    }
}
