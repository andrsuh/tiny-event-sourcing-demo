package ru.quipy.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("users")
class UserEntity(username: String, realName: String, password: String) {
    @Id
    var userId: UUID = UUID.randomUUID()
    lateinit var username: String
    lateinit var realName: String
    lateinit var password: String
}
