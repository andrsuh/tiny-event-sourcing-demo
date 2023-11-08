package ru.quipy.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document("users")
class UserEntity {

    @Id
    var id: String = UUID.randomUUID().toString()
    var username: String? = null
    var name: String? = null
    var password: String? = null

    constructor()

    constructor(username: String?, name: String?, password: String?) {
        this.username = username!!
        this.name = name!!
        this.password = password!!
    }
}
