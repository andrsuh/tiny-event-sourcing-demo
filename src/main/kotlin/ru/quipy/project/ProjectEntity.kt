package ru.quipy.project

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import ru.quipy.user.UserEntity
import java.sql.Timestamp
import java.util.UUID

@Document("project")
class ProjectEntity(username: String, realName: String, password: String) {
    @Id
    var id: UUID = UUID.randomUUID()
    lateinit var name: String
    //task
    @DBRef
    lateinit var participants: List<UserEntity>
    @DBRef
    lateinit var owner: UserEntity
    lateinit var created_at: Timestamp
    lateinit var updated_at: Timestamp
}
