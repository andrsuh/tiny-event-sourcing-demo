package ru.quipy.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("users")
class UserEntity {

    @Id
    var userId: UUID = UUID.randomUUID()
    lateinit var username: String
    lateinit var realName: String
    lateinit var password: String

    constructor()

    constructor(username: String, name: String, password: String) {
        this.username = username
        this.realName = name
        this.password = password
    }
}
