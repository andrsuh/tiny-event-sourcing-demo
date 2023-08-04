package ru.quipy.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.createProjectUser
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>) {
    @PostMapping("")
    fun create(
        @RequestParam nickname: String,
        @RequestParam name: String,
        @RequestParam password: String,
    ): UserCreatedEvent {
        return userEsService.create {
            it.createProjectUser(
                userId = UUID.randomUUID(),
                nickname = nickname,
                name = name,
                password = password,
            )
        }
    }

    @GetMapping("/{userId}")
    fun get(@PathVariable userId: UUID): UserAggregateState {
        return userEsService.getState(userId) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
}
