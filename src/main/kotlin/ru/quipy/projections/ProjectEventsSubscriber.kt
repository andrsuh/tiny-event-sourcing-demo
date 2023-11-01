package ru.quipy.projections

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.addToProject
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class ProjectEventsSubscriber(
    private val subscriptionsManager: AggregateSubscriptionsManager,
    private val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    val logger: Logger = LoggerFactory.getLogger(ProjectEventsSubscriber::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAggregate::class, "project::event-subscriber") {
            `when`(ProjectCreatedEvent::class) { projectEvent ->
                logger.info("Subscribe norm")
                userEsService.update(projectEvent.creatorId) {
                    it.addToProject(projectEvent.creatorId, projectEvent.projectId)
                }
            }
        }
    }
}
