package ru.quipy.controllers.userManagment

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.aggregates.userManagment.UserAggregate
import ru.quipy.commands.userManagment.user.create
import ru.quipy.core.EventSourcingService
import ru.quipy.dtos.user.CreateUserDto
import ru.quipy.dtos.user.UserDto
import ru.quipy.events.userManagment.user.UserCreatedEvent
import ru.quipy.services.userManaging.UserQueryHandlingService
import ru.quipy.states.userManagment.UserAggregateState
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    val userEventSourcingService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
    val userQueryHandlingService: UserQueryHandlingService,
) {
    @PostMapping
    fun createUser(@RequestBody createDto: CreateUserDto): UserCreatedEvent {
        return userEventSourcingService.create {
            it.create(
                UUID.randomUUID(),
                createDto.nickName,
                createDto.name
            )
        }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserDto? {
        return userQueryHandlingService
            .findUserById(userId)
    }

    @GetMapping("/")
    fun getUsersByProjectId(@RequestParam projectId: UUID) : List<UserDto>? {
        return userQueryHandlingService
            .findUsersByProjectId(projectId)
    }
}