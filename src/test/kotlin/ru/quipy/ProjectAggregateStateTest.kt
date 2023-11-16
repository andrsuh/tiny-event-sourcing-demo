package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.loadtest4j.LoadTester
import org.loadtest4j.Request
import org.loadtest4j.drivers.jmeter.JMeterBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import ru.quipy.api.TagCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.logic.*
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProjectAggregateStateTest {
    private val projectId = UUID.fromString("1b4087b6-0119-4451-b007-047ff7122eaa")
    private val userId = UUID.fromString("1b4087b6-0119-4451-b007-047ff7122eae")
    private val memberId = UUID.fromString("2b4087b6-0119-4451-b007-047ff7122eae")
    private val taskName = "Task1"
    private val statusColor = "4287f5"
    private val projectName = "MyProject"

    @Autowired
    private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    private lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun init() {
        mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(projectId)), "aggregate-project")
        mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(userId)), "aggregate-user")
        mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(memberId)), "aggregate-user")
        mongoTemplate.remove(Query.query(Criteria.where("snapshot.projectId").`is`(projectId)), "snapshots")

        userEsService.create {
            it.create(userId, "Anna", "Ann", "aaaaaaaa")
        }

        userEsService.create {
            it.create(memberId, "Linf", "LLL", "aaaaaaaa")
        }

        projectEsService.create {
            it.create(projectId, projectName, userId)
        }
    }


    @Test
    fun loadGeneralTestGetProjects() {

        val loadTester: LoadTester = JMeterBuilder.withUrl("http", "localhost", 3000)
            .withNumThreads(100)
            .withRampUp(5)
            .build();

        val result = loadTester.run(
            listOf(
                Request.get("/projects/${projectId}")
                    .withHeader("Accept", "*/*")
            )
        )

        val request = Request.get("/projects/${projectId}")
            .withHeader("Accept", "*/*")
        println("Запроосов в сек: ${result.diagnostics.requestsPerSecond}")
        println("Количество запросов: ${result.diagnostics.requestCount.total}")
        println("Percent OK: ${result.percentOk}")

        Assertions.assertTrue(result.percentOk > 99.99f)
    }

    @Test
    fun loadGeneralTestPostProjects() {

        val loadTester: LoadTester = JMeterBuilder.withUrl("http", "localhost", 3000)
            .withNumThreads(100)
            .withRampUp(5)
            .build();

        val result = loadTester.run(
            listOf(
                Request.post("/projects")
                    .withHeader("Accept", "*/*")
                    .withQueryParam("projectTitle", "MyProject")
                    .withQueryParam("creatorId", "${userId}")
            )
        )

        println("Запроосов в сек: ${result.diagnostics.requestsPerSecond}")
        println("Количество запросов: ${result.diagnostics.requestCount.total}")
        println("Percent OK: ${result.percentOk}")

        Assertions.assertTrue(result.percentOk > 99.99f)
    }


    fun formURlAddTag(): String {
        val oneStatusEvent: TagCreatedEvent = projectEsService.update(projectId) {
            it.createTag(UUID.randomUUID(),"DONE", statusColor)
        }

        val createdTaskEvent: TaskCreatedEvent = projectEsService.update(projectId) {
            it.addTask(UUID.randomUUID(), taskName)
        }

        return "/projects/${projectId}/tasks/${createdTaskEvent.taskId}/assign/tag/${oneStatusEvent.tagId}"
    }

    @Test
    fun loadGeneralTestAssignTagToTask() {
        val loadTester: LoadTester = JMeterBuilder.withUrl("http", "localhost", 3000)
            .withNumThreads(100)
            .withRampUp(5)
            .build();

        val result = loadTester.run(
            listOf(
                Request.post(formURlAddTag())
                    .withHeader("Accept", "*/*"),

                Request.get("/projects/${projectId}")
                    .withHeader("Accept", "*/*")
            )
        )
        println("Запроосов в сек: ${result.diagnostics.requestsPerSecond}")
        println("Количество запросов: ${result.diagnostics.requestCount.total}")
        println("Percent OK: ${result.percentOk}")

        Assertions.assertTrue(result.percentOk > 99.99f)
    }


    fun assignUserGetRequests(): List<Request> {
        val createdTaskEvent: TaskCreatedEvent = projectEsService.update(projectId) {
            it.addTask(UUID.randomUUID(), taskName)
        }

        projectEsService.update(projectId) {
            it.addUser(memberId)
        }

        val requests = listOf<Request>(
            Request.post("/projects/${projectId}/tasks/${createdTaskEvent.taskId}/assign/user/${memberId}")
                .withHeader("Accept", "*/*")
        )
        return requests
    }

    @Test
    fun loadGeneralTestAddUserToProjectGetProject() {
        val loadTester: LoadTester = JMeterBuilder.withUrl("http", "localhost", 3000)
            .withNumThreads(100)
            .withRampUp(5)
            .build();

        val result = loadTester.run(assignUserGetRequests())

        println("Запроосов в сек: ${result.diagnostics.requestsPerSecond}")
        println("Количество запросов: ${result.diagnostics.requestCount.total}")
        println("Percent OK: ${result.percentOk}")

        Assertions.assertTrue(result.percentOk > 99.99f)
    }
}