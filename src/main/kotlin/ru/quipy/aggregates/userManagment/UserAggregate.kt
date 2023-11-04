package ru.quipy.aggregates.userManagment

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate
import java.util.UUID

@AggregateType(aggregateEventsTableName = "users")
class UserAggregate(
) : Aggregate