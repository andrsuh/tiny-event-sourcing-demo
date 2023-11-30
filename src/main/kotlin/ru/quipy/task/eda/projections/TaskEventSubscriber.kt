package ru.quipy.task.eda.projections

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.task.eda.api.TaskAggregate
import javax.annotation.PostConstruct

@Service
class TaskEventSubscriber {

    val logger = LoggerFactory.getLogger(TaskEventSubscriber::class.java)

    @Autowired
    lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(TaskAggregate::class, "some-subscriber-name") {
            logger.debug("subscriber created for task")
        }
    }
}