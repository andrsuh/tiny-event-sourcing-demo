package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.create
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserAggregateTest {
    @Autowired
    private lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Test
    fun create_User() {
        val userId = UUID.randomUUID();
        val createdUser = userEsService.create { it.create(userId, "Zmushko", "Andrew", "SimplePassword")}
        val receivedUser = userEsService.getState(userId)
        Assertions.assertNotNull(receivedUser)
        Assertions.assertEquals(createdUser.surname, receivedUser?.surname)
        Assertions.assertEquals(createdUser.username, receivedUser?.username)
        Assertions.assertEquals(createdUser.password, receivedUser?.password)
    }
}
