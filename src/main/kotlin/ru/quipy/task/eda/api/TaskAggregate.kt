package ru.quipy.task.eda.api

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "task-aggregate")
class TaskAggregate: Aggregate {
}
