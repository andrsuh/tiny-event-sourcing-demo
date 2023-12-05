package ru.quipy.controller.endpoints

import org.springframework.web.bind.annotation.*
import ru.quipy.aggregate.project.ProjectAggregate
import ru.quipy.aggregate.project.events.ProjectAddedMemberEvent
import ru.quipy.aggregate.project.events.ProjectCreatedEvent
import ru.quipy.aggregate.project.events.ProjectRenamedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.project.ProjectAggregateState
import ru.quipy.logic.project.addMember
import ru.quipy.logic.project.create
import ru.quipy.logic.project.rename
import java.util.*

@RestController
@RequestMapping("/project")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {
    @PostMapping
    fun createProject(@RequestParam title: String, @RequestParam callerId: UUID) : ProjectCreatedEvent {
        return projectEsService.create{
            it.create(callerId, title)
        }
    }

    @PatchMapping("/{projectId}/changeName")
    fun rename(@PathVariable projectId: UUID, @RequestParam title: String, @RequestParam callerId: UUID)
    : ProjectRenamedEvent {
        return projectEsService.update(projectId){
            it.rename(callerId, title)
        }
    }

    @PostMapping("/{projectId}/addMember")
    fun addMember(@PathVariable projectId: UUID, @RequestParam callerId: UUID, @RequestParam userId: UUID
    ): ProjectAddedMemberEvent {
        return projectEsService.update(projectId){
            it.addMember(userId, callerId)
        }
    }
}