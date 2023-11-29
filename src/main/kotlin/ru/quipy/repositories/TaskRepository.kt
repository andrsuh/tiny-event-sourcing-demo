package ru.quipy.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.subscribers.projections.views.TaskViewDomain
import java.util.UUID

interface TaskRepository : MongoRepository<TaskViewDomain.Task, UUID> {
}