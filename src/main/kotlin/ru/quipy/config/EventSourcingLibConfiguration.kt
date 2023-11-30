package ru.quipy.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.aggregates.userManagment.UserAggregate
import ru.quipy.core.EventSourcingServiceFactory
import ru.quipy.services.projectManaging.ProjectQueryHandlingService
import ru.quipy.services.projectManaging.StatusQueryHandlingService
import ru.quipy.services.projectManaging.TaskQueryHandlingService
import ru.quipy.services.userManaging.UserQueryHandlingService
import ru.quipy.states.projectManagment.ProjectAggregateState
import ru.quipy.states.userManagment.UserAggregateState
import ru.quipy.streams.AggregateEventStreamManager
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.subscribers.logging.projectManagment.LoggingProjectEventsSubscriber
import ru.quipy.subscribers.projections.handlers.ProjectionProjectEventsSubscriber
import ru.quipy.subscribers.logging.userManagment.LoggingUserEventsSubscriber
import ru.quipy.subscribers.projections.handlers.ProjectionUserEventsSubscriber
import java.util.UUID
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
    private lateinit var subscriptionsManager: AggregateSubscriptionsManager

    @Autowired
    private lateinit var loggingProjectEventsSubscriber: LoggingProjectEventsSubscriber

    @Autowired
    private lateinit var loggingUserEventsSubscriber: LoggingUserEventsSubscriber

    @Autowired
    private lateinit var projectionProjectEventsSubscriber: ProjectionProjectEventsSubscriber

    @Autowired
    private lateinit var projectionUserEventsSubscriber: ProjectionUserEventsSubscriber

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Autowired
    private lateinit var eventStreamManager: AggregateEventStreamManager

    @Autowired
    private lateinit var projectQueryHandlingService: ProjectQueryHandlingService

    @Autowired
    private lateinit var statusQueryHandlingService: StatusQueryHandlingService

    @Autowired
    private lateinit var taskQueryHandlingService: TaskQueryHandlingService

    @Autowired
    private lateinit var userQueryHandlingService: UserQueryHandlingService

    /**
     * Use this object to create/update the aggregate
     */
    @Bean
    fun projectEventSourcingService() =
        eventSourcingServiceFactory.create<UUID, ProjectAggregate, ProjectAggregateState>()

    @Bean
    fun userEventSourcingService() =
        eventSourcingServiceFactory.create<UUID, UserAggregate, UserAggregateState>()

    @PostConstruct
    fun init() {
        // Demonstrates how to explicitly subscribe the instance of annotation based subscriber to some stream. See the [AggregateSubscriptionsManager]
        subscriptionsManager.subscribe<ProjectAggregate>(loggingProjectEventsSubscriber)

        subscriptionsManager.subscribe<UserAggregate>(loggingUserEventsSubscriber)

        subscriptionsManager.subscribe<ProjectAggregate>(projectionProjectEventsSubscriber)

        subscriptionsManager.subscribe<UserAggregate>(projectionUserEventsSubscriber)

        // Demonstrates how you can set up the listeners to the event stream
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