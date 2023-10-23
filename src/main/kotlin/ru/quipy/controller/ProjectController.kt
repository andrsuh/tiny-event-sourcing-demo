package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.lang.IllegalArgumentException
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String) : ProjectCreatedEvent {
        return projectEsService.create { it.createProject(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/status/createStatus/{statusName}")
    fun createStatus(@PathVariable projectId: UUID, @PathVariable statusName: String, @RequestParam color: String) : StatusCreatedEvent{
        return projectEsService.update(projectId) {
            it.createStatus(statusName, color)
        }
    }

//    @PostMapping("/{projectId}/{taskId}/addStatus/{statusId}")
//    fun assignStatusToTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @PathVariable statusId: UUID):
//            StatusAssignedToTaskEvent {
////        val task = taskEsService.getState(taskId)
////            ?: throw IllegalArgumentException("No such task: $taskId")
////        projectEsService.update(projectId)  {
////            it.assignStatusToTask(projectId, taskId, task.status)
////        }
////        return taskEsService.update(taskId){
////            it.assignStatusToTask(projectId, taskId, statusId)
////        }
//    }

    @DeleteMapping("/{projectId}/status/deleteStatus/{statusId}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID) : DeleteStatusEvent{
        return projectEsService.update(projectId) {
            it.deleteStatus(statusId)
        }
    }

    @PostMapping("/{projectId}/status/changeStatus/{statusId}/{newStatusName}")
    fun changeStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID, @PathVariable newStatusName: String) : ChangeStatusEvent{
        return projectEsService.update(projectId) {
            it.changeStatus(statusId, newStatusName)
        }
    }

    @PostMapping("/{projectId}/addUser/{userId}")
    fun addParticipantToProject(@PathVariable projectId: UUID, @PathVariable userId: UUID) : AddParticipantToProjectEvent{
        return projectEsService.update(projectId){
            it.addParticipantToProject(userId)
        }
    }
}
