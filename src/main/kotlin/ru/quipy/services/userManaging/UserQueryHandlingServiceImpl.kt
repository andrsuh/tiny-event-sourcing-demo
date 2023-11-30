package ru.quipy.services.userManaging

import org.springframework.stereotype.Component
import ru.quipy.dtos.user.UserDto
import ru.quipy.dtos.user.toDto
import ru.quipy.repositories.ProjectRepository
import ru.quipy.repositories.UserRepository
import java.util.UUID

@Component
class UserQueryHandlingServiceImpl(
    private val userRepository: UserRepository,
    private val projectRepository: ProjectRepository,
) : UserQueryHandlingService {
    override fun findUserById(id: UUID): UserDto? {
        val user = userRepository
            .findUserById(id) ?: return null

        return user.toDto()
    }

    override fun findUsersByProjectId(projectId: UUID): List<UserDto> {
        val project = projectRepository
            .findProjectById(
                projectId
            ) ?: throw NullPointerException()

        val users = userRepository
            .findUsersByIdIn(
                project.participantIds
            )

        if (users.isEmpty()) {
            return listOf()
        }

        return users
            .map {
                it.toDto()
            }
    }
}