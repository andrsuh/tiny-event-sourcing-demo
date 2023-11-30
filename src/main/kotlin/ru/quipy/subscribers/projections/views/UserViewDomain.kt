package ru.quipy.subscribers.projections.views

import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.domain.Unique
import java.util.UUID

class UserViewDomain {
    @Document("user-view")
    data class User(
        override val id: UUID,
        var name: String,
        val nickname: String,
    ) : Unique<UUID>
}