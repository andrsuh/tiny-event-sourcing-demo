package ru.quipy.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.api.user.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.user.UserAggregateState
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
    ) {

    @GetMapping("/{userId}")
    fun getTask(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }
}