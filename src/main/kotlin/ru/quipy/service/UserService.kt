package ru.quipy.service

import org.springframework.stereotype.Service
import ru.quipy.api.aggregate.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.state.UserAggregateState
import ru.quipy.projections.UserEventsSubscriber

import java.util.*

@Service
abstract class UserService (
    val userAccountCacheRepository: UserEventsSubscriber.UserAccountCacheRepository
    ) {
        fun getAllUsersName(): MutableSet<String> {
            val users = userAccountCacheRepository.findAll()
            val usersNameSet = mutableSetOf<String>()
            users.forEach{
                usersNameSet.add(it.userName)
            }
            return usersNameSet
        }
    }
