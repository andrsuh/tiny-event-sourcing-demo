package ru.quipy.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import java.util.*

@Service
class TaskValidationService @Autowired constructor(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    fun checkIfStatusExists(statusId: UUID, projectId: UUID): Boolean {
        return projectEsService.getState(projectId)?.projectStatuses?.get(statusId) != null
    }

    fun checkIfUserExists(userId: UUID): Boolean {
        return userEsService.getState(userId) != null
    }
}