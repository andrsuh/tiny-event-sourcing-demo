package ru.quipy.aggregates.projectManagment

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "projects")
class ProjectAggregate : Aggregate