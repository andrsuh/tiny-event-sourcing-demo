package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.aggregate.UserAggregate
import ru.quipy.api.event.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.commands.addProject
import ru.quipy.logic.state.UserAggregateState
import ru.quipy.logic.commands.changeName
import ru.quipy.logic.commands.create
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping()
    fun createUser(@RequestParam name: String, @RequestParam password: String) : UserCreatedEvent {
        return userEsService.create { it.create(UUID.randomUUID(), name, password) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }

    @PostMapping("/{userId}/name")
    fun changeName(@PathVariable userId: UUID, @RequestParam name: String) : UserNameChangedEvent {
        return userEsService.update(userId) {
            it.changeName(name)
        }
    }
    @PostMapping("/{userId}/projects")
    fun addProject(@PathVariable userId: UUID, @RequestParam projectId: UUID) : UserProjectAddedEvent {
        return userEsService.update(userId) {
            it.addProject(projectId)
        }
    }
}