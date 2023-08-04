package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskStatusDeletedEvent
import ru.quipy.api.TaskStatusesOrderSetEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*
import kotlin.collections.LinkedHashSet

@RestController
@RequestMapping("/projects/{projectId}/taskStatuses")
class TaskStatusController(val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>) {
    @PostMapping("")
    fun create(
        @PathVariable projectId: UUID,
        @RequestParam name: String,
        @RequestParam colorHexCode: String,
    ): TaskStatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.createTaskStatus(name, Color(colorHexCode))
        }
    }

    @DeleteMapping("/{statusId}")
    fun delete(@PathVariable projectId: UUID, @PathVariable statusId: UUID): TaskStatusDeletedEvent {
        return projectEsService.update(projectId) {
            it.deleteTaskStatus(statusId)
        }
    }

    @PutMapping("/setOrder")
    fun setOrder(
        @PathVariable projectId: UUID,
        @RequestParam statusesIds: LinkedHashSet<UUID>,
    ): TaskStatusesOrderSetEvent {
        return projectEsService.update(projectId) {
            it.setTaskStatusesOrder(statusesIds)
        }
    }
}
