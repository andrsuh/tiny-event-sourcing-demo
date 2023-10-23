package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.domain.Event
import ru.quipy.dto.ProjectDto
import ru.quipy.dto.StatusDto
import ru.quipy.dto.UserDto
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @GetMapping("")
    fun getAllProjects() : List<ProjectAggregateState> {
        throw IllegalAccessException()
    }

    @GetMapping("/{projectId}")
    fun getProject(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("")
    fun createProject(@RequestBody projectDto: ProjectDto) : Event<ProjectAggregate> {
        return projectEsService.create {
            it.createProject(
                projectDto.projectTitle,
                projectDto.creatorId,
                projectDto.description
            )
        }
    }

    @PostMapping("/{projectId}/statuses/")
    fun createStatus(@PathVariable projectId: UUID, @RequestBody statusDto: StatusDto) : StatusCreatedEvent {
        return projectEsService.update(projectId) {
            it.createStatus(
                projectId,
                statusDto.statusName,
                statusDto.statusColor
            )
        }
    }

    @DeleteMapping("/{projectId}/statuses/{statusId}")
    fun deleteStatus(@PathVariable projectId: UUID, @PathVariable statusId: UUID) : StatusDeletedEvent {
        return projectEsService.update(projectId) {
            it.deleteStatus(projectId, statusId)
        }
    }

}