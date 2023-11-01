package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {
    @PostMapping("/{username}")
    fun createUser(
        @RequestParam nickname: String,
        @RequestParam fullname: String,
        @RequestParam password: String
    ): UserCreatedEvent {
        return userEsService.create {
            it.create(
                UUID.randomUUID(),
                nickname,
                fullname,
                password
            )
        }
    }
}