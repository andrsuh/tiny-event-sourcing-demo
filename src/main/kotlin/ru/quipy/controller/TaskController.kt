package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.TaskDto
import ru.quipy.dto.UserDto
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {
    @GetMapping("")
    fun getAllTasks() : List<TaskAggregateState> {
        throw IllegalAccessException()
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID) : TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

    @PostMapping("")
    fun createTask(@RequestBody taskDto: TaskDto) : TaskCreatedEvent {
        return taskEsService.create {
            it.createTask(
                taskTitle = taskDto.taskTitle,
                description = taskDto.description,
                statusId = UUID.fromString("default"),    
                projectId = taskDto.projectId
            )
        }
    }

    @PostMapping("/{taskId}/executors/{userId}")
    fun addExecutor(@PathVariable taskId: UUID, @PathVariable userId : UUID) : TaskExecutorAddedEvent {
        return taskEsService.update(taskId) {
            it.addExecutor(taskId, userId)
        }
    }

    @DeleteMapping("/{taskId}/executors/{userId}")
    fun removeExecutor(@PathVariable taskId: UUID,  @PathVariable userId : UUID) : TaskExecutorRemovedEvent {
        return taskEsService.update(taskId) {
            it.removeExecutor(taskId, userId)
        }
    }
    @PatchMapping("/{taskId}/status")
    fun setStatus(@PathVariable taskId : UUID, @RequestBody statusId : UUID) : StatusSetEvent {
        return taskEsService.update(taskId) {
            it.setStatus(taskId, statusId)
        }
    }
}