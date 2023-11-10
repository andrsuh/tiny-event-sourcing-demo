package ru.quipy.controller

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.quipy.api.UserAggregate
import ru.quipy.api.user.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.user.create
import java.util.*
import javax.print.attribute.standard.JobOriginatingUserName


@RestController
@RequestMapping("/user")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {

    @PostMapping("/{userName}/{userNickName}/{userPassword}")
    fun createrUser( @PathVariable userName: String , @PathVariable userNickName: String , @PathVariable userPassword: String): UserCreatedEvent {
        return userEsService.create { it.create(UUID.randomUUID(), userName, userNickName, userPassword) }
    }

    @GetMapping("/{userID}")
    fun getUser(@PathVariable userID: UUID) : UserAggregateState? {
        return userEsService.getState(userID)
    }

}