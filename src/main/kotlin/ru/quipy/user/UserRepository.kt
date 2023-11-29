package ru.quipy.user

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<UserEntity, String> {
    @Query("{username: ?0}")
    fun findByUsername(username: String): UserEntity?
}