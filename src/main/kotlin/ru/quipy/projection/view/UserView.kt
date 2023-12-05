package ru.quipy.projection.view

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.*

class UserView {
    @Document(collection = "user-view")
    data class User(
        @Id override val id: UUID,
        val userName: String,
        val createdAt: Long = System.currentTimeMillis(),
    ) : Unique<UUID>

    data class ProjectUsers(
        @Id override val id: UUID,
        val userId: UUID,
        val projectId: UUID
    ) : Unique<UUID>
}