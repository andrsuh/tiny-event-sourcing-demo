package ru.quipy.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.ExecutorAssignedToTaskEvent
import ru.quipy.api.ExecutorRetractedFromTaskEvent
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectMemberAddedEvent
import ru.quipy.api.ProjectTitleChangedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAssignedToTaskEvent
import ru.quipy.api.TaskStatusCreatedEvent
import ru.quipy.api.TaskStatusRemovedEvent
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.ProjectService
import ru.quipy.logic.TaskAggregateState
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    private val projectService: ProjectService,
) {

    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: UUID): ProjectCreatedEvent =
        projectService.createProject(projectTitle, creatorId)

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ProjectAggregateState? =
        projectService.getProject(projectId)

    @PatchMapping("/{projectId}")
    fun changeTitle(@PathVariable projectId: UUID, @RequestParam title: String): ProjectTitleChangedEvent =
        projectService.changeTitle(projectId, title)

    @PostMapping("/{projectId}/members")
    fun addProjectMember(@PathVariable projectId: UUID, @RequestParam memberId: UUID): ProjectMemberAddedEvent =
        projectService.addProjectMember(projectId, memberId)

    @PostMapping("/{projectId}/taskStatuses")
    fun addTaskStatus(@PathVariable projectId: UUID, @RequestParam name: String): TaskStatusCreatedEvent =
        projectService.addTaskStatus(projectId, name)

    @DeleteMapping("/{projectId}/taskStatuses/{taskStatusId}")
    fun removeTaskStatus(@PathVariable projectId: UUID, @PathVariable taskStatusId: UUID): TaskStatusRemovedEvent =
        projectService.removeTaskStatus(projectId, taskStatusId)

    @PostMapping("/{projectId}/tasks/")
    fun createTask(
        @PathVariable projectId: UUID,
        @RequestParam taskName: String,
        @RequestParam creatorId: UUID,
    ): TaskCreatedEvent = projectService.createTask(projectId, taskName, creatorId)

    @GetMapping("/{projectId}/tasks/{taskId}")
    fun getTask(@PathVariable projectId: UUID, @PathVariable taskId: UUID): TaskAggregateState? =
        projectService.getTask(projectId, taskId)

    @PatchMapping("/{projectId}/tasks/{taskId}")
    fun assignTaskStatus(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam taskStatusId: UUID,
    ): TaskStatusAssignedToTaskEvent = projectService.assignTaskStatus(projectId, taskId, taskStatusId)

    @PostMapping("/{projectId}/tasks/{taskId}/executors")
    fun assignExecutor(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam executorId: UUID,
    ): ExecutorAssignedToTaskEvent = projectService.assignExecutor(projectId, taskId, executorId)

    @DeleteMapping("/{projectId}/tasks/{taskId}/executors")
    fun retractExecutor(
        @PathVariable projectId: UUID,
        @PathVariable taskId: UUID,
        @RequestParam executorId: UUID,
    ): ExecutorRetractedFromTaskEvent = projectService.retractExecutor(projectId, taskId, executorId)

}