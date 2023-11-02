package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import java.util.*

@RestController
@RequestMapping("/users")
class UserController (val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping()
    fun createUser(@RequestParam name: String, @RequestParam nickname: String, @RequestParam password: String) : UserCreatedEvent {
        return userEsService.create{ it.create(UUID.randomUUID(), name, nickname, password) }
    }

}