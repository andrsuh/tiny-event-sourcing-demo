package ru.quipy.projections.repository

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import ru.quipy.projections.UserEventsSubscriber
import java.util.*

@Repository
interface UserAccountCacheRepository: MongoRepository<UserEventsSubscriber.UserAccount, UUID>