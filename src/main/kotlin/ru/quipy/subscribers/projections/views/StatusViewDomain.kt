package ru.quipy.subscribers.projections.views

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.UUID

class StatusViewDomain {
    @Document("status-view")
    data class Status(
        @Id
        override val id: StatusId,
        val name: String,
        val colorCode: String,
        val taskIds: MutableSet<UUID> = mutableSetOf(),
    ) : Unique<StatusId>

    data class StatusId(
        val projectId: UUID,
        val innerId: UUID
    )
}