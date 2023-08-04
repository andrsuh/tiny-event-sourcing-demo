package ru.quipy.serivce

import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskStatusDeletedEvent
import ru.quipy.api.TaskStatusesOrderSetEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@Service
class TaskStatusService(val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>) {
    fun createStatus(projectId: UUID, name: String, color: Color): TaskStatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTaskStatus(name, color)
        }
    }

    fun deleteStatus(projectId: UUID, statusId: UUID): TaskStatusDeletedEvent {
        return projectEsService.update(projectId) {
            it.deleteTaskStatus(statusId)
        }
    }

    fun setStatusesOrder(projectId: UUID, statusesIds: LinkedHashSet<UUID>): TaskStatusesOrderSetEvent {
        return projectEsService.update(projectId) {
            it.setTaskStatusesOrder(statusesIds)
        }
    }
}
