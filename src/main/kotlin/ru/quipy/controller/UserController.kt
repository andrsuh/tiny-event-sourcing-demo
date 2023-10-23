package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.UserDto
import ru.quipy.logic.*
import java.util.*

@RestController
@RequestMapping("/users")
class UserControllerController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {
    @GetMapping("/")
    fun getAllUsers() : List<UserAggregateState> {
        throw IllegalAccessException()
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }
    
    @PostMapping("/")
    fun createUser(@RequestBody userDto: UserDto) : UserCreatedEvent {
        return userEsService.create {
            it.createUser(
                userDto.nickname, 
                userDto.userName
            )
        }
    }
}