package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
        val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @PostMapping("/{projectTitle}")
    fun createProject(@PathVariable projectTitle: String, @RequestParam creatorId: String): ProjectCreatedEvent {
        return projectEsService.create { it.create(UUID.randomUUID(), projectTitle, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID): ProjectAggregateState? {
//        return projectEsService.getState(projectId)
        return getProjectNames(listOf(projectId)).single()
    }
    @GetMapping("/get-all")
    fun getProjectNames(@RequestBody projectIds: List<UUID>): List<ProjectAggregateState?> {
        return projectIds.map { projectId -> projectEsService.getState(projectId) }
    }

    @PostMapping("/{projectId}")
    fun addUserToProject(@PathVariable projectId: UUID, @RequestParam userId: UUID): UserAssignedToProjectEvent {
        return projectEsService.update(projectId) {
            it.assignUserToProject(userId)
        }
    }

    @GetMapping("/{projectId}/get-project-members")
    fun getProjectMembers(@PathVariable projectId: UUID): List<ProjectMemberEntity> {
        return projectEsService.getState(projectId)?.projectMembers?.map { it.value } ?: emptyList()
    }

    @PostMapping("/{projectId}/createTag")
    fun createTag(@PathVariable projectId: UUID, @RequestParam name: String, @RequestParam color: String): TagCreatedEvent {
        return projectEsService.update(projectId) { it.createTag(name, color) }
    }

//    @PostMapping("/{projectId}/tasks/{taskName}")
//    fun createTask(@PathVariable projectId: UUID, @PathVariable taskName: String): TaskCreatedEvent {
//        return projectEsService.update(projectId) {
//            it.addTask(taskName)
//        }
//    }
}