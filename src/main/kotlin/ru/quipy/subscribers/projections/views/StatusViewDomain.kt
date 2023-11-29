package ru.quipy.subscribers.projections.views

import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.UUID

class StatusViewDomain {
    @Document("status-view")
    data class Status(
        override val id: UUID,
        val name: String,
        val colorCode: String,
        val tasks: MutableSet<UUID>,
    ) : Unique<UUID>
}