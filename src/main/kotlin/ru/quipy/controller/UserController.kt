package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import ru.quipy.projections.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserController (val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
                      val userRepository: UserRepository,
                      val userProjectsRepository: UserProjectsRepository
) {
    @PostMapping()
    fun createUser(@RequestParam name: String, @RequestParam nickname: String, @RequestParam password: String) : UserCreatedEvent {
        val users = userRepository.findAll()
        if (users.isNotEmpty() && users.single{x -> x.nickname == nickname} != null)
            throw Exception("User with nickname ${nickname} already exists")
        return userEsService.create{ it.create(UUID.randomUUID(), name, nickname, password) }
    }

    @PostMapping("login")
    fun login(@RequestParam nickname: String, @RequestParam password: String) : User {
        val user = userRepository.findAll().single{ x -> x.nickname == nickname} ?: throw Exception("Invalid nickname")
        val userAggregate = userEsService.getState(user.userId) ?: throw Exception("Fail to get aggregate")
        if (userAggregate.password == password)
            return user
        else
            throw Exception("Invalid password")
    }

    @GetMapping("/all")
    fun getUsers() : List<User> {
        return userRepository.findAll()
    }

    @GetMapping("/{userId}/projects")
    fun getUserProject(@PathVariable userId: UUID) : UserProjects {
        return userProjectsRepository.findById(userId).get()
    }
}