package ru.quipy.controller.endpoints

import org.springframework.web.bind.annotation.*
import ru.quipy.aggregate.user.UserAggregate
import ru.quipy.aggregate.user.events.UserChangedNameEvent
import ru.quipy.aggregate.user.events.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.user.UserAggregateState
import ru.quipy.logic.user.changeUserName
import ru.quipy.logic.user.createUser
import java.util.*

@RestController
@RequestMapping("/user")
class UserController(
    val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
) {
    @PostMapping
    fun createUser(@RequestParam userName: String): UserCreatedEvent {
        return userEsService.create{
            it.createUser(userName)
        }
    }

    @PatchMapping fun changeUserName(@RequestParam userName: String, @RequestParam callerId: UUID):
            UserChangedNameEvent {
        return userEsService.update(callerId){
            it.changeUserName(userName, callerId)
        }
    }
}