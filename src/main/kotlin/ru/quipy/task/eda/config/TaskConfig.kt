package ru.quipy.task.eda.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.streams.AggregateEventStreamManager
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.task.eda.api.TaskAggregate
import ru.quipy.task.eda.logic.TaskAggregateState
import ru.quipy.task.eda.projections.AnnotationBasedTaskEventSubscriber
import java.util.*
import javax.annotation.PostConstruct

@Configuration
class TaskConfig {

    private val logger = LoggerFactory.getLogger(TaskConfig::class.java)

    @Autowired
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    private lateinit var taskEventSubscriber: AnnotationBasedTaskEventSubscriber

    @Autowired
    private lateinit var eventStreamManager: AggregateEventStreamManager

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun userEsService() = eventSourcingServiceFactory.create<UUID, TaskAggregate, TaskAggregateState>()

    @PostConstruct
    fun init() {
        subscriptionsManager.subscribe<TaskAggregate>(taskEventSubscriber)

        eventStreamManager.maintenance {
            onRecordHandledSuccessfully { streamName, eventName ->
                logger.info("Stream $streamName successfully processed record of $eventName")
            }

            onBatchRead { streamName, batchSize ->
                logger.info("Stream $streamName read batch size: $batchSize")
            }
        }
    }

    @Bean
    fun userESService() = eventSourcingServiceFactory.create<UUID, TaskAggregate, TaskAggregateState>()
}
