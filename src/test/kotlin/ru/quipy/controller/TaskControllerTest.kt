package ru.quipy.controller

import io.restassured.module.mockmvc.kotlin.extensions.Extract
import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*


@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>

    @Autowired
    lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    private lateinit var projectId: UUID
    private lateinit var defaultStatus: TaskStatus

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
        defaultStatus = projectEsService.getState(projectId)!!.defaultStatus()
    }

    @Test
    fun `create task`() {
        val creatorId = createProjectUser()

        val taskId: String = Given {
            mockMvc(mockMvc)
            param("name", "A task")
            param("creatorId", creatorId.toString())
        } When {
            post("/projects/$projectId/tasks")
        } Then {
            status(HttpStatus.OK)
            body(
                "taskId", not(blankString()),
                "projectId", equalTo(projectId.toString()),
                "taskName", equalTo("A task"),
                "creatorId", equalTo(creatorId.toString()),
            )
        } Extract {
            path("taskId")
        }

        val task = taskEsService.getState(UUID.fromString(taskId))
        assertThat(task, notNullValue())
        if (task != null) {
            assertThat(task.name, equalTo("A task"))
            assertThat(task.projectId, equalTo(projectId))
            assertThat(task.assignees.size, equalTo(0))
            assertThat(task.statusId, notNullValue())
        }
    }

    @Test
    fun `get task`() {
        val taskId = UUID.randomUUID()
        taskEsService.create {
            it.createTask(
                taskId = taskId,
                projectId = projectId,
                name = "A task",
                creatorId = UUID.randomUUID(),
                statusId = defaultStatus.id,
            )
        }

        Given {
            mockMvc(mockMvc)
        } When {
            get("/projects/$projectId/tasks/$taskId")
        } Then {
            status(HttpStatus.OK)
            body(
                "id", equalTo(taskId.toString()),
                "name", equalTo("A task"),
                "assignees.size()", equalTo(0),
            )
        }
    }

    @Test
    fun `cannot get task from wrong project`() {
        val taskId = UUID.randomUUID()
        taskEsService.create {
            it.createTask(
                taskId = taskId,
                projectId = projectId,
                name = "A task",
                creatorId = UUID.randomUUID(),
                statusId = defaultStatus.id,
            )
        }

        Given {
            mockMvc(mockMvc)
        } When {
            get("/projects/%s/tasks/%s".format(UUID.randomUUID(), taskId))
        } Then {
            status(HttpStatus.NOT_FOUND)
        }
    }

    @Test
    fun `cannot get nonexistent task`() {
        Given {
            mockMvc(mockMvc)
        } When {
            get("/projects/%s/tasks/%s".format(UUID.randomUUID(), UUID.randomUUID()))
        } Then {
            status(HttpStatus.NOT_FOUND)
        }
    }

    @Test
    fun `assign task`() {
        val taskId = UUID.randomUUID()
        val assigneeId = createProjectUser()
        taskEsService.create {
            it.createTask(
                taskId = taskId,
                projectId = projectId,
                name = "A task",
                creatorId = createProjectUser(),
                statusId = defaultStatus.id,
            )
        }

        Given {
            mockMvc(mockMvc)
            param("assigneeId", assigneeId.toString())
        } When {
            post("/projects/$projectId/tasks/$taskId/assignees")
        } Then {
            status(HttpStatus.OK)
            body(
                "taskId", equalTo(taskId.toString()),
                "assigneeId", equalTo(assigneeId.toString()),
            )
        }

        val state = taskEsService.getState(taskId)!!
        assertThat(state.assignees, contains(assigneeId))
    }

    @Test
    fun `cannot assign task the second time to the same user`() {
        val taskId = UUID.randomUUID()
        val assigneeId = createProjectUser()
        taskEsService.create {
            it.createTask(
                taskId = taskId,
                projectId = projectId,
                name = "A task",
                creatorId = createProjectUser(),
                statusId = defaultStatus.id,
            )
        }
        taskEsService.update(taskId) { it.assignTask(assigneeId) }

        Given {
            mockMvc(mockMvc)
            param("assigneeId", assigneeId.toString())
        } When {
            post("/projects/$projectId/tasks/$taskId/assignees")
        } Then {
            status(HttpStatus.CONFLICT)
        }

        val state = taskEsService.getState(taskId)!!
        assertThat(state.assignees.size, equalTo(1))
    }

    @Test
    fun rename() {
        val taskId = UUID.randomUUID()
        taskEsService.create {
            it.createTask(
                taskId = taskId,
                projectId = projectId,
                name = "A task",
                creatorId = UUID.randomUUID(),
                statusId = defaultStatus.id,
            )
        }

        Given {
            mockMvc(mockMvc)
            param("newName", "Renamed")
        } When {
            put("/projects/$projectId/tasks/$taskId/rename")
        } Then {
            status(HttpStatus.OK)
            body(
                "taskId", equalTo(taskId.toString()),
                "newName", equalTo("Renamed"),
            )
        }

        val state = taskEsService.getState(taskId)!!
        assertThat(state.name, equalTo("Renamed"))
    }

    @Test
    fun `set status`() {
        val taskId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        projectEsService.create {
            it.createProject(
                projectId=projectId,
                name = "A project",
                creatorId = UUID.randomUUID(),
            )
        }
        val statusCreatedEvent = projectEsService.update(projectId) {
            it.createTaskStatus(
                name = "DONE",
                color = Color("#009900"),
            )
        }
        val statusId = statusCreatedEvent.statusId
        taskEsService.create {
            it.createTask(
                taskId = taskId,
                projectId = projectId,
                name = "A task",
                creatorId = UUID.randomUUID(),
                statusId = defaultStatus.id,
            )
        }

        Given {
            mockMvc(mockMvc)
            param("statusId", statusId.toString())
        } When {
            put("/projects/$projectId/tasks/$taskId/setStatus")
        } Then {
            status(HttpStatus.OK)
            body(
                "taskId", equalTo(taskId.toString()),
                "statusId", equalTo(statusId.toString()),
            )
        }

        val state = taskEsService.getState(taskId)!!
        assertThat(state.statusId, equalTo(statusId))
    }

    @Test
    fun `cannot set status that does not belong to the project`() {
        val taskId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val statusId = UUID.randomUUID()
        projectEsService.create {
            it.createProject(
                projectId=projectId,
                name = "A project",
                creatorId = UUID.randomUUID(),
            )
        }
        taskEsService.create {
            it.createTask(
                taskId = taskId,
                projectId = projectId,
                name = "A task",
                creatorId = UUID.randomUUID(),
                statusId = defaultStatus.id,
            )
        }

        Given {
            mockMvc(mockMvc)
            param("statusId", statusId.toString())
        } When {
            put("/projects/$projectId/tasks/$taskId/setStatus")
        } Then {
            status(HttpStatus.UNPROCESSABLE_ENTITY)
        }

        val state = taskEsService.getState(taskId)!!
        assertThat(state.statusId, not(equalTo(statusId)))
    }


    @Test
    fun `cannot set the same status`() {
        val taskId = UUID.randomUUID()
        taskEsService.create {
            it.createTask(
                taskId = taskId,
                projectId = projectId,
                name = "A task",
                creatorId = UUID.randomUUID(),
                statusId = defaultStatus.id,
            )
        }
        val currentStatusId = taskEsService.getState(taskId)!!.statusId

        Given {
            mockMvc(mockMvc)
            param("statusId", currentStatusId.toString())
        } When {
            put("/projects/$projectId/tasks/$taskId/setStatus")
        } Then {
            status(HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

    private fun createProjectUser(): UUID {
        val userId = UUID.randomUUID()
        userEsService.create {
            it.createProjectUser(
                userId = userId,
                name = "A user",
                nickname = "a-user",
                password = "secret",
            )
        }
        projectEsService.update(projectId) {
            it.addProjectMember(userId)
        }
        return userId
    }
}
