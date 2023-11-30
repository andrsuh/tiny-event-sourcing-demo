package ru.quipy.services.projectManaging

import ru.quipy.dtos.task.TaskDto
import ru.quipy.dtos.task.TaskInfoDto
import java.util.UUID

interface TaskQueryHandlingService {
    fun findTaskById(projectId: UUID, innerId: UUID): TaskDto?
    fun findTasksByProjectId(projectId: UUID): List<TaskInfoDto>
}