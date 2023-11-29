package ru.quipy.user.eda.projections

import org.springframework.stereotype.Service
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.user.eda.api.UserAggregate


@Service
@AggregateSubscriber(aggregateClass = UserAggregate::class, subscriberName = "user-subs-stream")
class AnnotationBasedUserEventSubscriber {
}
