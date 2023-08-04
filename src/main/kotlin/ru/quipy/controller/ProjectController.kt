package ru.quipy.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectMemberAddedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.TaskStatus
import ru.quipy.logic.addProjectMember
import ru.quipy.logic.createProject
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>) {
    @PostMapping("")
    fun create(@RequestParam name: String, @RequestParam creatorId: UUID): ProjectCreatedEvent {
        return projectEsService.create { it.createProject(UUID.randomUUID(), name, creatorId) }
    }

    @GetMapping("/{projectId}")
    fun get(@PathVariable projectId: UUID): Project {
        val state = projectEsService.getState(projectId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return Project.fromState(state)
    }

    @PostMapping("/{projectId}/members")
    fun addMember(@PathVariable projectId: UUID, @RequestParam memberId: UUID): ProjectMemberAddedEvent {
        return projectEsService.update(projectId) { it.addProjectMember(memberId) }
    }

    data class Project(
        val id: UUID,
        val name: String,
        val creatorId: UUID,
        val members: Set<UUID>,
        val taskStatuses: List<TaskStatus>,
    ) {
        companion object {
            fun fromState(state: ProjectAggregateState): Project {
                return Project(
                    id = state.getId(),
                    name = state.name,
                    creatorId = state.creatorId,
                    members = state.members,
                    taskStatuses = state.taskStatuses.values.sortedBy { it.ordinalNumber }
                )
            }
        }
    }
}
