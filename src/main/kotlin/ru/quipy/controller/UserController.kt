package ru.quipy.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.createUser


@RestController
@RequestMapping("/users")
class UserController(
        val usersEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("")
    fun createTask(@RequestBody body: CreateUserRequest): TaskCreatedEvent {
        return usersEsService.create { it.createUser(UUID.randomUUID(), body.userName, body.nickname, body.password) }
    }
}

data class CreateUserRequest (
        val userId: UUID,
        val userName: String,
        val nickname: String,
        val password: String
)