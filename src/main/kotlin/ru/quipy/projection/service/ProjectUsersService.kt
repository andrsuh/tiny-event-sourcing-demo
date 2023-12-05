package ru.quipy.projection.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.aggregate.project.ProjectAggregate
import ru.quipy.aggregate.project.events.ProjectAddedMemberEvent
import ru.quipy.aggregate.project.events.ProjectCreatedEvent
import ru.quipy.projection.dto.UserDto
import ru.quipy.projection.repository.ProjectUsersProjectionRepository
import ru.quipy.projection.repository.UserProjectionRepository
import ru.quipy.projection.view.UserView
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.*

@Service
@AggregateSubscriber(aggregateClass = ProjectAggregate::class, subscriberName = "project-users-subscriber")
class ProjectUsersService(
    val userProjectionRepository: UserProjectionRepository,
    val projectUsersRepository: ProjectUsersProjectionRepository
) {
    val logger: Logger = LoggerFactory.getLogger(ProjectUsersService::class.java)

    @SubscribeEvent
    fun projectCreatedSubscriber(event: ProjectCreatedEvent) {
        logger.info("Project created: {}", event.title)
        projectUsersRepository.save(
            UserView.ProjectUsers(
                UUID.randomUUID(),
                event.creatorId,
                event.projectId
            )
        )
    }

    @SubscribeEvent
    fun userAddedToProjectSubscriber(event: ProjectAddedMemberEvent) {
        logger.info("User added to project: {}", event.name)
        projectUsersRepository.save(
            UserView.ProjectUsers(
                UUID.randomUUID(),
                event.userId,
                event.projectId
            )
        )
    }

    fun findUsersByProjectId(projectId: UUID): List<UserDto> {
        val projectUsers = projectUsersRepository.findByProjectId(projectId)
        val userIds = projectUsers.map { it.userId }
        val users = userProjectionRepository.findAllById(userIds)
        return users.map { UserDto(it.id, it.userName) }
    }
}