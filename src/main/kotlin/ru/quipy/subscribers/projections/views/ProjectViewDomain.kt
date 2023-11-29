package ru.quipy.subscribers.projections.views

import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.UUID

class ProjectViewDomain {
    @Document("project-view")
    data class Project(
        override val id: UUID,
        val name: String,
        val tasks: MutableSet<UUID>,
        val participants: MutableSet<UUID>,
        val statuses: MutableSet<UUID>,
        val creatorId: UUID,
    ) : Unique<UUID>
}