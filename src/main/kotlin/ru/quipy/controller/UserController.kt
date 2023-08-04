package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserCreatedEvent
import ru.quipy.logic.UserAggregateState
import ru.quipy.serivce.UserService
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(val userService: UserService) {
    @PostMapping("")
    fun create(
        @RequestParam nickname: String,
        @RequestParam name: String,
        @RequestParam password: String,
    ): UserCreatedEvent {
        return userService.createUser(nickname, name, password)
    }

    @GetMapping("/{userId}")
    fun get(@PathVariable userId: UUID): UserAggregateState {
        return userService.getUserById(userId)
    }
}
