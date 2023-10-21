package ru.quipy.logic

import ru.quipy.api.UserAggregate
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    override fun getId(): UUID? {
        TODO("Not yet implemented")
    }

}