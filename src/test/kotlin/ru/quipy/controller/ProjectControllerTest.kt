package ru.quipy.controller

import io.restassured.module.mockmvc.kotlin.extensions.Extract
import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.servlet.MockMvc
import ru.quipy.api.ProjectAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.addProjectMember
import ru.quipy.logic.createProject
import java.util.*


@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Test
    fun `create project`() {
        val creatorId = UUID.randomUUID().toString()

        val projectId: String = Given {
            mockMvc(mockMvc)
            param("name", "First Project")
            param("creatorId", creatorId)
        } When {
            post("/projects")
        } Then {
            status(HttpStatus.OK)
            body(
                "projectId", not(blankString()),
                "projectName", equalTo("First Project"),
                "creatorId", equalTo(creatorId),
            )
        } Extract {
            path("projectId")
        }

        val project = projectEsService.getState(UUID.fromString(projectId))
        assertThat(project, notNullValue())
        if (project != null) {
            assertThat(project.creatorId, equalTo(UUID.fromString(creatorId)))
            assertThat(project.name, equalTo("First Project"))
            assertThat(project.members.size, equalTo(0))
            assertThat(project.taskStatuses.size, equalTo(1))
            assertThat(project.taskStatuses.values.first().name, equalTo("CREATED"))
            assertThat(project.taskStatuses.values.first().isDefault, equalTo(true))
        }
    }

    @Test
    fun `get project`() {
        val id = UUID.randomUUID()
        val creatorId = UUID.randomUUID()
        projectEsService.create {
            it.createProject(
                projectId = id,
                name = "First Project",
                creatorId = creatorId,
            )
        }

        Given {
            mockMvc(mockMvc)
        } When {
            get("/projects/$id")
        } Then {
            status(HttpStatus.OK)
            body(
                "id", equalTo(id.toString()),
                "name", equalTo("First Project"),
                "creatorId", equalTo(creatorId.toString()),
                "members.size()", equalTo(0),
                "taskStatuses.size()", equalTo(1),
                "taskStatuses[0].isDefault", equalTo(true),
                "taskStatuses[0].name", equalTo("CREATED"),
            )
        }
    }

    @Test
    fun `cannot get nonexistent project`() {
        Given {
            mockMvc(mockMvc)
        } When {
            get("/projects/" + UUID.randomUUID())
        } Then {
            status(HttpStatus.NOT_FOUND)
        }
    }

    @Test
    fun `add member`() {
        val projectId = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        projectEsService.create {
            it.createProject(
                projectId = projectId,
                name = "A Project",
                creatorId = UUID.randomUUID(),
            )
        }

        Given {
            mockMvc(mockMvc)
            param("memberId", memberId)
        } When {
            post("/projects/$projectId/members")
        } Then {
            status(HttpStatus.OK)
            body(
                "projectId", equalTo(projectId.toString()),
                "memberId", equalTo(memberId.toString()),
            )
        }

        val project = projectEsService.getState(projectId)!!
        assertThat(project.members.size, equalTo(1))
        assertThat(project.members, hasItem(memberId))
    }

    @Test
    fun `cannot add same member twice`() {
        val projectId = UUID.randomUUID()
        val memberId = UUID.randomUUID()
        projectEsService.create {
            it.createProject(
                projectId = projectId,
                name = "A Project",
                creatorId = UUID.randomUUID(),
            )
        }
        projectEsService.update(projectId) {
            it.addProjectMember(memberId)
        }

        Given {
            mockMvc(mockMvc)
            param("memberId", memberId)
        } When {
            post("/projects/$projectId/members")
        } Then {
            status(HttpStatus.CONFLICT)
        }

        val project = projectEsService.getState(projectId)!!
        assertThat(project.members.size, equalTo(1))
        assertThat(project.members, hasItem(memberId))
    }
}
