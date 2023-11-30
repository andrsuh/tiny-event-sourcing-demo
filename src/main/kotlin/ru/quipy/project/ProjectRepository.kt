package ru.quipy.project

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ru.quipy.user.UserEntity

@Repository
interface ProjectRepository : MongoRepository<UserEntity, String> {
}