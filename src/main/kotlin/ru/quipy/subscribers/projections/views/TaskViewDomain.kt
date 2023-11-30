package ru.quipy.subscribers.projections.views

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.UUID

class TaskViewDomain {
    @Document("task-view")
    data class Task(
        @Id
        override val id: TaskId,
        var name: String,
        var statusId: UUID,
        val assigneeIds: MutableSet<UUID> = mutableSetOf()
    ) : Unique<TaskId>

    data class TaskId(
        val projectId: UUID,
        val innerId: UUID
    )
}