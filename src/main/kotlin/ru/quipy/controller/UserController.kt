package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
) {

    @PostMapping
    fun createUser(@RequestParam login: String, @RequestParam password: String): UserCreatedEvent {
        // TODO check of there is no already a user with the same login

        return userEsService.create {
            it.create(login = login, password = password)
        }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID): UserAggregateState? = userEsService.getState(userId)

}
