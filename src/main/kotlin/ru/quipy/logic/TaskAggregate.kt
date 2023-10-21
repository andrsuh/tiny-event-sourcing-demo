package ru.quipy.logic

import ru.quipy.api.TaskAggregate
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState : AggregateState<UUID, TaskAggregate> {
    override fun getId(): UUID? {
        TODO("Not yet implemented")
    }

}