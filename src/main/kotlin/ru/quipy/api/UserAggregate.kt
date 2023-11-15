package ru.quipy.api

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

/**
 * @author Andrew Zmushko (andrewzmushko@gmail.com)
 */
@AggregateType(aggregateEventsTableName = "aggregate-user")
class UserAggregate : Aggregate