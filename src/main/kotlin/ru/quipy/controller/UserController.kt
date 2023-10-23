package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.lang.IllegalArgumentException
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
) {
    @PostMapping("/{userName}")
    fun createUser(@PathVariable userName: String, @RequestParam password: String, @RequestParam role: String) : UserCreatedEvent {
        return userEsService.create { it.createUser(UUID.randomUUID(), userName, password, role) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }

    @PostMapping("/changeUsername/{userId}")
    fun changeUsername(@PathVariable userId: UUID, @RequestParam newUserName: String) : ChangeUserNameEvent{
        return userEsService.update(userId) {
            it.changeUserName(userId, newUserName)
        }
    }

}