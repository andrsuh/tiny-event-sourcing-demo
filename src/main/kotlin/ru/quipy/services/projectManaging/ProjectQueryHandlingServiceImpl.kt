package ru.quipy.services.projectManaging

import org.springframework.stereotype.Component
import ru.quipy.dtos.project.ProjectDto
import ru.quipy.dtos.project.ProjectInfoDto
import ru.quipy.dtos.project.toInfoDto
import ru.quipy.dtos.status.StatusDto
import ru.quipy.dtos.status.toInfoDto
import ru.quipy.dtos.task.TaskDtoWithoutStatus
import ru.quipy.dtos.task.toInfoDto
import ru.quipy.dtos.user.toDto
import ru.quipy.repositories.ProjectRepository
import ru.quipy.repositories.StatusRepository
import ru.quipy.repositories.TaskRepository
import ru.quipy.repositories.UserRepository
import ru.quipy.subscribers.projections.views.ProjectViewDomain
import java.util.UUID

@Component
class ProjectQueryHandlingServiceImpl(
    private val projectRepository: ProjectRepository,
    private val statusRepository: StatusRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
) : ProjectQueryHandlingService {
    override fun findProjectById(id: UUID): ProjectDto? {
        val project = projectRepository
            .findProjectById(id) ?: return null

        val users = userRepository
            .findUsersByIdIn(project.participantIds)

        val statuses = statusRepository
            .findStatusesById_ProjectId(id)

        val tasks = taskRepository
            .findTasksById_ProjectId(id)

        return ProjectDto(
            project.toInfoDto(),
            users
                .map { user ->
                    user.toDto()
                },
            statuses
                .map { status ->
                    StatusDto(
                        status.toInfoDto(),
                        tasks
                            .asSequence()
                            .filter { task ->
                                task.statusId == status.id.innerId
                            }
                            .map { task ->
                                TaskDtoWithoutStatus(
                                    task.toInfoDto(),
                                    users
                                        .asSequence()
                                        .filter { user ->
                                            task.assigneeIds.contains(user.id)
                                        }
                                        .map { user ->
                                            user.toDto()
                                        }
                                        .toList()
                                )
                            }
                            .toList()
                    )
                }
        )
    }

    override fun findProjectsByFilters(creatorId: UUID?, participantId: UUID?): List<ProjectInfoDto> {
        val projects: List<ProjectViewDomain.Project>
        if (creatorId != null && participantId != null) {
            projects = projectRepository
                .findProjectsByCreatorIdAndParticipantIdsContaining(
                    creatorId,
                    participantId
                )
        } else if (creatorId != null) {
            projects = projectRepository
                .findProjectsByCreatorId(creatorId)
        } else if (participantId != null) {
            projects = projectRepository
                .findProjectsByParticipantIdsContaining(participantId)
        } else {
            projects = projectRepository
                .findAll()
        }

        if (projects.isEmpty()) {
            return listOf()
        }

        return projects.map {
            it.toInfoDto()
        }
    }


}