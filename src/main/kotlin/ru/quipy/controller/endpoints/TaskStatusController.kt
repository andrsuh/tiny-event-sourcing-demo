package ru.quipy.controller.endpoints

import org.springframework.web.bind.annotation.*
import ru.quipy.aggregate.project.ProjectAggregate
import ru.quipy.aggregate.project.events.TaskStatusCreatedEvent
import ru.quipy.aggregate.project.events.TaskStatusDeletedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.ProjectAggregateState
import ru.quipy.logic.project.createTaskStatus
import ru.quipy.logic.project.deleteTaskStatus
import java.util.*

@RestController
@RequestMapping("/projects/{projectId}/status")
class ProjectTaskStatusController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @PostMapping
    fun createTaskStatus(@PathVariable projectId: UUID, @RequestParam callerId: UUID, @RequestBody dto: CreateTaskStatusDto)
    : TaskStatusCreatedEvent {
        return projectEsService.update(projectId){
            it.createTaskStatus(callerId, dto.title, dto.colorRgb)
        }
    }

    data class CreateTaskStatusDto(
        val title: String,
        val colorRgb: Int
    )

    @DeleteMapping("/{taskStatusId}")
    fun deleteTaskStatus(@PathVariable projectId: UUID, @PathVariable taskStatusId: UUID, @RequestParam callerId: UUID)
    : TaskStatusDeletedEvent {
        return projectEsService.update(projectId){
            it.deleteTaskStatus(callerId, taskStatusId)
        }
    }
}