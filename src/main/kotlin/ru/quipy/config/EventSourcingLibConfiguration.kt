package ru.quipy.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.aggregate.project.ProjectAggregate
import ru.quipy.aggregate.user.UserAggregate
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.logic.project.ProjectAggregateState
import ru.quipy.logic.user.UserAggregateState
import ru.quipy.streams.AggregateEventStreamManager
import java.util.*
import javax.annotation.PostConstruct

/**
 * This files contains some configurations that you might want to have in your project. Some configurations are
 * made in for the sake of demonstration and not required for the library functioning. Usually you can have even
 * more minimalistic config
 *
 * Take into consideration that we autoscan files searching for Aggregates, Events and StateTransition functions.
 * Autoscan enabled via [event.sourcing.auto-scan-enabled] property.
 *
 * But you can always disable it and register all the classes manually like this
 * ```
 * @Autowired
 * private lateinit var aggregateRegistry: AggregateRegistry
 *
 * aggregateRegistry.register(ProjectAggregate::class, ProjectAggregateState::class) {
 *     registerStateTransition(TagCreatedEvent::class, ProjectAggregateState::tagCreatedApply)
 *     registerStateTransition(TaskCreatedEvent::class, ProjectAggregateState::taskCreatedApply)
 *     registerStateTransition(TagAssignedToTaskEvent::class, ProjectAggregateState::tagAssignedApply)
 * }
 * ```
 */
@Configuration
class EventSourcingLibConfiguration {

    private val logger = LoggerFactory.getLogger(EventSourcingLibConfiguration::class.java)

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var eventStreamManager: AggregateEventStreamManager

    @Bean
    fun projectEsService() = eventSourcingServiceFactory.create<UUID, ProjectAggregate, ProjectAggregateState>()

    @Bean
    fun userEsService() = eventSourcingServiceFactory.create<UUID, UserAggregate, UserAggregateState>()

    @PostConstruct
    fun init() {
        eventStreamManager.maintenance {
            onRecordHandledSuccessfully { streamName, eventName ->
                logger.info("Stream $streamName successfully processed record of $eventName")
            }

            onBatchRead { streamName, batchSize ->
                logger.info("Stream $streamName read batch size: $batchSize")
            }
        }
    }

}