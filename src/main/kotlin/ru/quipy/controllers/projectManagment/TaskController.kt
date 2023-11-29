package ru.quipy.controllers.projectManagment

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.commands.projectManagment.project.addTask
import ru.quipy.commands.projectManagment.project.assignToTask
import ru.quipy.commands.projectManagment.project.changeTask
import ru.quipy.core.EventSourcingService
import ru.quipy.dtos.projectManagment.task.CreateTaskDto
import ru.quipy.dtos.projectManagment.task.TaskDto
import ru.quipy.dtos.projectManagment.task.UpdateTaskDto
import ru.quipy.dtos.projectManagment.task.getTaskDto
import ru.quipy.dtos.projectManagment.user.UserDto
import ru.quipy.events.projectManagment.project.AssigneeAddedEvent
import ru.quipy.events.projectManagment.project.TaskChangedEvent
import ru.quipy.events.projectManagment.project.TaskCreatedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

@RestController
@RequestMapping("/projects")
class TaskController(
    val projectEventSourcingService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @PostMapping("/{projectId}/tasks/")
    fun addTask(@PathVariable projectId: UUID, @RequestBody createDto: CreateTaskDto): TaskCreatedEvent {
        return projectEventSourcingService.update(projectId) {
            it.addTask(
                UUID.randomUUID(),
                createDto.name,
            )
        }
    }

    @GetMapping("/{projectId}/tasks/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskDto? {
        return projectEventSourcingService
            .getState(projectId)
            ?.getTaskDto(taskId)
    }

    @PostMapping("/{projectId}/tasks/{taskId}/assignees")
    fun assigneeToTask(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestBody assigneeDto: UserDto
    ): AssigneeAddedEvent {
        return projectEventSourcingService.update(projectId) {
            it.assignToTask(
                assigneeDto.id,
                taskId
            )
        }
    }


    @PatchMapping("/{projectId}/tasks/{taskId}")
    fun updateTask(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestBody updateDto: UpdateTaskDto
    ): TaskChangedEvent {
        return projectEventSourcingService.update(projectId) {
            it.changeTask(
                taskId,
                updateDto.name,
                updateDto.statusId
            )
        }
    }
}