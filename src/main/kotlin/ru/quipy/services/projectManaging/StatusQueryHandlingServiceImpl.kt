package ru.quipy.services.projectManaging

import org.springframework.stereotype.Component
import ru.quipy.dtos.status.StatusDto
import ru.quipy.dtos.status.StatusInfoDto
import ru.quipy.dtos.status.toInfoDto
import ru.quipy.dtos.task.TaskDtoWithoutStatus
import ru.quipy.dtos.task.toInfoDto
import ru.quipy.dtos.user.toDto
import ru.quipy.repositories.StatusRepository
import ru.quipy.repositories.TaskRepository
import ru.quipy.repositories.UserRepository
import ru.quipy.subscribers.projections.views.UserViewDomain
import java.util.UUID

@Component
class StatusQueryHandlingServiceImpl(
    private val statusRepository: StatusRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
) : StatusQueryHandlingService {
    override fun findStatusById(projectId: UUID, innerId: UUID): StatusDto? {
        val status = statusRepository
            .findStatusById_ProjectIdAndId_InnerId(projectId, innerId) ?: return null

        val tasks = taskRepository
            .findTasksById_ProjectIdAndStatusId(projectId, innerId)

        val users : Map<UUID, List<UserViewDomain.User>> = tasks
            .associate { task ->
                Pair(
                    task.id.innerId,
                    userRepository
                        .findUsersByIdIn(
                            task.assigneeIds
                        )
                )
            }

        return StatusDto(
            status.toInfoDto(),
            tasks
                .map { task ->
                    TaskDtoWithoutStatus(
                        task.toInfoDto(),
                        users
                            .getValue(task.id.innerId)
                            .map { user ->
                                user.toDto()
                            }
                    )
                }
        )
    }

    override fun findStatusesByProjectId(projectId: UUID): List<StatusInfoDto> {
        val statuses = statusRepository
            .findStatusesById_ProjectId(projectId)

        if (statuses.isEmpty()) {
            return listOf()
        }

        return statuses
            .map {
                it.toInfoDto()
            }
    }
}