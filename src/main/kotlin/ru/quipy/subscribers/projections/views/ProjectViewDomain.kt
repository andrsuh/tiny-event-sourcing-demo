package ru.quipy.subscribers.projections.views

import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

class ProjectViewDomain {
    @Document("project-view")
    data class Project(
        override val id: UUID,
        val name: String,
        val creatorId: UUID,
        val participants: MutableSet<UUID> = mutableSetOf(creatorId),
        val tasks: MutableSet<UUID> = mutableSetOf(),
        val statuses: MutableSet<UUID> = mutableSetOf(ProjectAggregateState.DEFAULT_STATUS.id)
    ) : Unique<UUID>
}