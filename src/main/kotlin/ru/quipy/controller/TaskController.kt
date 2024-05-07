package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {

    @PostMapping("/{projectId}")
    fun createTask(@PathVariable projectId: UUID, @RequestParam title: String): TaskCreatedEvent {
        return taskEsService.create { it.createTask(projectId, title) }
    }

    @PostMapping("/changeTitle/{taskId}")
    fun changeTitle(@PathVariable taskId: UUID, @RequestParam title: String): TaskTitleChangedEvent {
        return taskEsService.update(taskId) { it.changeTitle(title) }
    }

    @PostMapping("/assignee/{taskId}")
    fun assignee(@PathVariable taskId: UUID, @RequestParam participantId: UUID): ParticipantAssignedToTaskEvent {
        return taskEsService.update(taskId) { it.assignee(participantId) }
    }

    @PostMapping("/setTaskStatus/{taskId}")
    fun setTaskStatus(@PathVariable taskId: UUID, @RequestParam statusId: UUID): TaskStatusChangedEvent {
        return taskEsService.update(taskId) { it.setTaskStatus(statusId) }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID): TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

}
