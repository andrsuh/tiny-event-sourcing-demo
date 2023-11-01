package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusChangedEvent
import ru.quipy.api.UserAddedToTaskEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.assignStatusToTask
import ru.quipy.logic.assignUser
import ru.quipy.logic.createTask
import ru.quipy.service.TaskValidationService
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController (
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    val validationService: TaskValidationService
) {

    @PostMapping("/{taskName}")
    fun createTask(@PathVariable taskName: String, @RequestParam projectId: String, @RequestParam description: String, @RequestParam creatorId: String) : TaskCreatedEvent {
        return taskEsService.create { it.createTask(UUID.fromString(projectId), taskName, description, UUID.fromString(creatorId)) }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID) : TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

    @PutMapping("/addStatus/{taskId}")
    fun assignStatus(@PathVariable taskId: String, @RequestParam statusId: String) : TaskStatusChangedEvent {
        val task = getTask(UUID.fromString(taskId))
        val projectId = task?.projectId ?: error("No project for task")
        val isStatusExist = validationService.checkIfStatusExists(UUID.fromString(statusId), projectId)
        if (!isStatusExist) error("No status with given id")

        return taskEsService.update(UUID.fromString(taskId)){
            it.assignStatusToTask(UUID.fromString(statusId), UUID.fromString(taskId))
        }
    }

    @PutMapping("/assignUser/{taskId}")
    fun assignUser(@PathVariable taskId: String, @RequestParam userId: String) : UserAddedToTaskEvent {
        val isUserExists = validationService.checkIfUserExists(UUID.fromString(userId))
        if (!isUserExists) error("No user with given id")

        return taskEsService.update(UUID.fromString(taskId)){
            it.assignUser(UUID.fromString(userId), UUID.fromString(taskId))
        }
    }
}