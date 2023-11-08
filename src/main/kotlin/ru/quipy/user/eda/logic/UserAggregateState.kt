package ru.quipy.user.eda.logic

import ru.quipy.domain.AggregateState
import ru.quipy.user.eda.api.UserAggregate
import java.math.BigDecimal
import java.util.UUID

class UserAggregateState: AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    private var username: String? = ""
    private var password: String? = ""
    private var name: String? = ""

    override fun getId(): UUID {
        return this.userId
    }
}
