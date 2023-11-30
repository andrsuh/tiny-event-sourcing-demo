package ru.quipy.task.eda.projections

import org.springframework.stereotype.Service
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.task.eda.api.TaskAggregate


@Service
@AggregateSubscriber(aggregateClass = TaskAggregate::class, subscriberName = "task-subs-stream")
class AnnotationBasedTaskEventSubscriber {
}
