package ru.quipy.serivce

import org.springframework.stereotype.Service
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.ProjectMemberAddedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.NoSuchEntity
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.addProjectMember
import ru.quipy.logic.createProject
import java.util.*

@Service
class ProjectService(val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>) {
    fun createProject(name: String, creatorId: UUID): ProjectCreatedEvent {
        return projectEsService.create { it.createProject(UUID.randomUUID(), name, creatorId) }
    }

    fun getProjectById(projectId: UUID): ProjectAggregateState {
        return projectEsService.getState(projectId) ?: throw NoSuchEntity("Project $projectId does not exist")
    }

    fun addProjectMember(projectId: UUID, memberId: UUID): ProjectMemberAddedEvent {
        return projectEsService.update(projectId) { it.addProjectMember(memberId) }
    }

    fun projectExists(projectId: UUID) = projectEsService.getState(projectId) != null

    fun isProjectMember(projectId: UUID, userId: UUID): Boolean {
        return getProjectById(projectId).members.contains(userId)
    }

    fun projectHasTaskStatus(projectId: UUID, statusId: UUID): Boolean {
        return getProjectById(projectId).taskStatuses.containsKey(statusId)
    }
}
