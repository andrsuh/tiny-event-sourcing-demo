package ru.quipy.logic

import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
// Service's business logic
class TaskAggregateState : AggregateState<UUID, TaskAggregate> {

    private lateinit var taskId: UUID
    var createAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var taskTitle: String
    lateinit var creatorId: String
    lateinit var tagId: String

    override fun getId() = taskId


    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
    }
}