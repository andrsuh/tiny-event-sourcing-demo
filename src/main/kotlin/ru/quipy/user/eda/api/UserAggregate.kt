package ru.quipy.user.eda.api

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "user-aggregate")
class UserAggregate: Aggregate {
}
