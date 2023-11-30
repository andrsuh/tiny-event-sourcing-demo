package ru.quipy.services.projectManaging

import org.springframework.stereotype.Component
import ru.quipy.dtos.status.toInfoDto
import ru.quipy.dtos.task.TaskDto
import ru.quipy.dtos.task.TaskInfoDto
import ru.quipy.dtos.task.toInfoDto
import ru.quipy.dtos.user.toDto
import ru.quipy.repositories.StatusRepository
import ru.quipy.repositories.TaskRepository
import ru.quipy.repositories.UserRepository
import java.util.UUID

@Component
class TaskQueryHandlingServiceImpl(
    private val statusRepository: StatusRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
) : TaskQueryHandlingService {
    override fun findTaskById(projectId: UUID, innerId: UUID): TaskDto? {
        val task = taskRepository
            .findTaskById_ProjectIdAndId_InnerId(
                projectId,
                innerId
            ) ?: return null

        val status = statusRepository
            .findStatusById_ProjectIdAndId_InnerId(
                projectId,
                task.statusId
            ) ?: throw NullPointerException()

        val users = userRepository
            .findUsersByIdIn(
                task.assigneeIds
            )

        return TaskDto(
            task.toInfoDto(),
            users
                .map {
                    it.toDto()
                },
            status.toInfoDto()
        )
    }

    override fun findTasksByProjectId(projectId: UUID): List<TaskInfoDto> {
        val tasks = taskRepository
            .findTasksById_ProjectId(projectId)

        if (tasks.isEmpty()) {
            return listOf()
        }

        return tasks
            .map {
                it.toInfoDto()
            }
    }
}