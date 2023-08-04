package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskStatusDeletedEvent
import ru.quipy.api.TaskStatusesOrderSetEvent
import ru.quipy.logic.Color
import ru.quipy.serivce.TaskStatusService
import java.util.*

@RestController
@RequestMapping("/projects/{projectId}/taskStatuses")
class TaskStatusController(val taskStatusService: TaskStatusService) {
    @PostMapping("")
    fun create(
        @PathVariable projectId: UUID,
        @RequestParam name: String,
        @RequestParam colorHexCode: String,
    ): TaskStatusCreatedEvent {
        return taskStatusService.createStatus(projectId, name, Color(colorHexCode))
    }

    @DeleteMapping("/{statusId}")
    fun delete(@PathVariable projectId: UUID, @PathVariable statusId: UUID): TaskStatusDeletedEvent {
        return taskStatusService.deleteStatus(projectId, statusId)
    }

    @PutMapping("/setOrder")
    fun setOrder(
        @PathVariable projectId: UUID,
        @RequestParam statusesIds: LinkedHashSet<UUID>,
    ): TaskStatusesOrderSetEvent {
        return taskStatusService.setStatusesOrder(projectId, statusesIds)
    }
}
