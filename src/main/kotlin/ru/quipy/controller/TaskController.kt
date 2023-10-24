package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
    val tasksEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>
) {

    @PostMapping("")
    fun createTask(@RequestBody body: CreateTaskRequest): TaskCreatedEvent {
        return tasksEsService.create { it.createTask(UUID.randomUUID(), body.projectId, body.name, body.description, body.deadline) }
    }

    @GetMapping("/{taskId}")
    fun getTask(@PathVariable taskId: UUID): TaskAggregateState? {
        return tasksEsService.getState(taskId)
    }

    @PatchMapping("/{taskId}")
    fun updateTask(@PathVariable taskId: UUID, @RequestBody body: UpdateTaskRequest): TaskUpdatedEvent {
        return tasksEsService.update(taskId) { it.updateTask(body.name, body.description, body.deadline) }
    }
    @PostMapping("/{taskId}/executors")
    fun addExecutor(@PathVariable taskId: UUID, @RequestBody body: AddExecutorRequest): ExecutorAddedEvent{
        return tasksEsService.update(taskId) { it.addExecutor(body.userId) }
    }

    @PostMapping("/{taskId}/statuses/{statusId}")
    fun assignStatus(@PathVariable taskId: UUID, @PathVariable statusId: UUID): StatusAssignedToTaskEvent {
        return tasksEsService.update(taskId) { it.assignStatus(statusId) }
    }
    @DeleteMapping("/{taskId}/statuses")
    fun removeStatus(@PathVariable taskId: UUID): StatusRemovedFromTaskEvent{
        return tasksEsService.update(taskId) { it.removeStatus() }
    }
}

data class CreateTaskRequest (
    val projectId: UUID,
    val name: String,
    val description: String,
    val deadline: Date
)

data class UpdateTaskRequest (
    val name: String,
    val description: String,
    val deadline: Date
)

data class AddExecutorRequest (
    val userId: UUID
)
