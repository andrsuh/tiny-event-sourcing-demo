package ru.quipy.controller

import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import ru.quipy.api.ProjectAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.Color
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.createProject
import ru.quipy.logic.createTaskStatus
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class TaskStatusControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    lateinit var projectId: UUID

    @BeforeEach
    fun setUp() {
        val event = projectEsService.create {
            it.createProject(
                projectId = UUID.randomUUID(),
                name = "A Project",
                creatorId = UUID.randomUUID(),
            )
        }
        projectId = event.projectId
    }

    @Test
    fun `create status`() {
        Given {
            mockMvc(mockMvc)
            param("name", "DONE")
            param("colorHexCode", "#00aa00")
        } When {
            post("/projects/$projectId/taskStatuses")
        } Then {
            status(HttpStatus.OK)
            body(
                "statusId", not(blankString()),
            )
        }

        val project = projectEsService.getState(projectId)!!
        assertThat(project.taskStatuses.size, equalTo(2))
        assertTrue(
            project.taskStatuses.values.any {
                !it.isDefault && it.name == "DONE" && it.color == Color("#00aa00") && it.ordinalNumber == 2
            }
        )
    }

    @Test
    fun `cannot create status with the same name`() {
        projectEsService.update(projectId) {
            it.createTaskStatus(name = "DONE", color = Color("#00aa00"))
        }

        Given {
            mockMvc(mockMvc)
            param("name", "DONE")
            param("colorHexCode", "#00aa00")
        } When {
            post("/projects/$projectId/taskStatuses")
        } Then {
            status(HttpStatus.CONFLICT)
        }

        val project = projectEsService.getState(projectId)!!
        assertThat(project.taskStatuses.size, equalTo(2))
    }

    @Test
    fun `delete status`() {
        val createEvent = projectEsService.update(projectId) {
            it.createTaskStatus(name = "DONE", color = Color("#00aa00"))
        }

        Given {
            mockMvc(mockMvc)
        } When {
            delete("/projects/$projectId/taskStatuses/${createEvent.statusId}")
        } Then {
            status(HttpStatus.OK)
        }

        val project = projectEsService.getState(projectId)!!
        assertThat(project.taskStatuses.size, equalTo(1))
        assertTrue(project.taskStatuses.values.none { it.name == "DONE" })
    }
    @Test
    fun `cannot delete nonexistent status`() {
        Given {
            mockMvc(mockMvc)
        } When {
            delete("/projects/$projectId/taskStatuses/${UUID.randomUUID()}")
        } Then {
            status(HttpStatus.NOT_FOUND)
        }

        val project = projectEsService.getState(projectId)!!
        assertThat(project.taskStatuses.size, equalTo(1))
    }

    @Test
    fun `cannot delete default status`() {
        val defaultStatus = projectEsService.getState(projectId)!!
            .taskStatuses
            .values
            .first { it.isDefault }

        Given {
            mockMvc(mockMvc)
        } When {
            delete("/projects/$projectId/taskStatuses/${defaultStatus.id}")
        } Then {
            status(HttpStatus.UNPROCESSABLE_ENTITY)
        }

        val project = projectEsService.getState(projectId)!!
        assertThat(project.taskStatuses.size, equalTo(1))
    }

    @Test
    fun `set statuses order`() {
        projectEsService.update(projectId) {
            it.createTaskStatus(name = "DONE", color = Color("#00aa00"))
        }
        val statuses = projectEsService.getState(projectId)!!
            .taskStatuses
            .values

        Given {
            mockMvc(mockMvc)
            param("statusesIds", statuses.first { it.name == "DONE" }.id)
            param("statusesIds", statuses.first { it.name == "CREATED" }.id)
        } When {
            put("/projects/$projectId/taskStatuses/setOrder")
        } Then {
            status(HttpStatus.OK)
        }

        val newStatuses = projectEsService.getState(projectId)!!
            .taskStatuses
            .values
        assertThat(newStatuses.first { it.name == "DONE" }.ordinalNumber, equalTo(1))
        assertThat(newStatuses.first { it.name == "CREATED" }.ordinalNumber, equalTo(2))
    }
}
