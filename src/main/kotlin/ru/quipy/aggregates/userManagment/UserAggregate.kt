package ru.quipy.aggregates.userManagment

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "users")
class UserAggregate : Aggregate