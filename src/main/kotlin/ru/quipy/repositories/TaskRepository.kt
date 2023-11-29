package ru.quipy.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.subscribers.projections.views.TaskViewDomain

interface TaskRepository : MongoRepository<TaskViewDomain.Task, TaskViewDomain.TaskId> {
}