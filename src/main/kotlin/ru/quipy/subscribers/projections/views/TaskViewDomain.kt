package ru.quipy.subscribers.projections.views

import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.UUID

class TaskViewDomain {
    @Document("task-view")
    data class Task(
        override val id: UUID,
        var name: String,
        var statusId: UUID,
        val assigneeIds: MutableSet<UUID>
    ) : Unique<UUID>
}