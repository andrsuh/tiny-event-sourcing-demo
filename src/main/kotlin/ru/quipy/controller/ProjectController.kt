package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectMemberAddedEvent
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.TaskStatus
import ru.quipy.serivce.ProjectService
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(val projectService: ProjectService) {
    @PostMapping("")
    fun create(@RequestParam name: String, @RequestParam creatorId: UUID): ProjectCreatedEvent {
        return projectService.createProject(name, creatorId)
    }

    @GetMapping("/{projectId}")
    fun get(@PathVariable projectId: UUID): Project {
        return Project.fromState(projectService.getProjectById(projectId))
    }

    @PostMapping("/{projectId}/members")
    fun addMember(@PathVariable projectId: UUID, @RequestParam memberId: UUID): ProjectMemberAddedEvent {
        return projectService.addProjectMember(projectId, memberId)
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
