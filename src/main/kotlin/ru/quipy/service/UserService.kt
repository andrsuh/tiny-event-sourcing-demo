package ru.quipy.service

import org.springframework.stereotype.Service
import ru.quipy.api.aggregate.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.state.UserAggregateState
import ru.quipy.projections.UserAccount
import ru.quipy.projections.UserAccountCacheRepository

import java.util.*

@Service
class UserService (
    val userAccountCacheRepository: UserAccountCacheRepository
    ) {

        fun getAllUsers(): MutableList<UserAccount> {
            return userAccountCacheRepository.findAll()
        }

        fun getAllUsersName(): MutableSet<String> {
            val users = userAccountCacheRepository.findAll()
            val usersNameSet = mutableSetOf<String>()
            users.forEach{
                usersNameSet.add(it.userName)
            }
            return usersNameSet
        }

        fun getAllUsersId(): MutableSet<UUID> {
            val users = userAccountCacheRepository.findAll()
            val usersIdSet = mutableSetOf<UUID>()
            users.forEach{
                usersIdSet.add(it.userId)
            }
            return usersIdSet
        }
    }
