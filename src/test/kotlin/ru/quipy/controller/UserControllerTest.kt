package ru.quipy.controller

import io.restassured.module.mockmvc.kotlin.extensions.Extract
import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.createUser
import java.util.*


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Test
    fun `create user`() {
        val userId: String = Given {
            mockMvc(mockMvc)
            param("nickname", "first_user")
            param("name", "First User")
            param("password", "secret")
        } When {
            post("/users")
        } Then {
            status(HttpStatus.OK)
            body(
                "userId", not(blankString()),
                "nickname", equalTo("first_user"),
                "userName", equalTo("First User"),
                "password", equalTo("secret"),
            )
        } Extract {
            path("userId")
        }

        val user = userEsService.getState(UUID.fromString(userId))
        assertThat(user, notNullValue())
        if (user != null) {
            assertThat(user.name, equalTo("First User"))
            assertThat(user.nickname, equalTo("first_user"))
            assertThat(user.password, equalTo("secret"))
        }
    }

    @Test
    fun `get user`() {
        val id = UUID.randomUUID()
        userEsService.create {
            it.createUser(
                userId = id,
                nickname = "first_user",
                name = "First User",
                password = "secret",
            )
        }

        Given {
            mockMvc(mockMvc)
        } When {
            get("/users/$id")
        } Then {
            status(HttpStatus.OK)
            body(
                "id", equalTo(id.toString()),
                "nickname", equalTo("first_user"),
                "name", equalTo("First User"),
                "password", equalTo("secret"),
            )
        }
    }

    @Test
    fun `cannot get nonexistent user`() {
        Given {
            mockMvc(mockMvc)
        } When {
            get("/users/" + UUID.randomUUID())
        } Then {
            status(HttpStatus.NOT_FOUND)
        }
    }
}
