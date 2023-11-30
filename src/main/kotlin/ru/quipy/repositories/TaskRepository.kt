package ru.quipy.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import ru.quipy.subscribers.projections.views.TaskViewDomain
import java.util.UUID

interface TaskRepository : MongoRepository<TaskViewDomain.Task, TaskViewDomain.TaskId> {
    fun findTaskById_ProjectIdAndId_InnerId(projectId: UUID, innerId: UUID) : TaskViewDomain.Task?
    fun findTasksById_ProjectId(projectId: UUID) : List<TaskViewDomain.Task>
    fun findTasksById_ProjectIdAndStatusId(projectId: UUID, statusId: UUID) : List<TaskViewDomain.Task>
}