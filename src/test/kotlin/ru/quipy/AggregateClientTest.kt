package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.loadtest4j.LoadTester
import org.loadtest4j.Request
import org.loadtest4j.Result
import org.loadtest4j.drivers.jmeter.JMeterBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import ru.quipy.utils.*
import java.util.*
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AggregateClientTest {

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
        mongoTemplate.remove(Query.query(Criteria.where("snapshot.projectId").`is`(projectId)), "snapshots")

        userEsService.create {
            it.create(userId, createTestUsername(1), createTestNickname(1), createTestPassword(1))
        }
        projectEsService.create {
            it.create(projectId, createTestProjectName(1), userId)
        }
    }

    fun buildUrl(): String {
        val oneStatusEvent = projectEsService.update(projectId) {
            it.createTag(createTestTagName(Random.nextInt(1,1000)), createTestTagColor(Random.nextInt(1,1000)))
        }
        val createdTaskEvent: TaskCreatedEvent = projectEsService.update(projectId) {
            it.createTask(UUID.randomUUID(), createTestTaskTitle(Random.nextInt(1,1000)), projectId, oneStatusEvent.tagId, userId)
        }

        return "/projects/${projectId}/${createdTaskEvent.taskId}/set-status/${oneStatusEvent.tagId}"
    }

    fun loadGeneralTest(numThreads: Int, rampUp: Int): Result {
        val loadTester: LoadTester = loadTesterBuilder
            .withNumThreads(numThreads)
            .withRampUp(rampUp)
            .build()

        val result1 = listOf(Request.put(buildUrl()).withHeader("Accept", "*/*"))
        result1.forEach { println(it.path) }

        val result = loadTester.run(listOf(Request.put(buildUrl()).withHeader("Accept", "*/*")))

        buildTable(result)
        Assertions.assertTrue(result.percentOk > 99.99f)
        Assertions.assertTrue(result.diagnostics.requestsPerSecond >= (numThreads / rampUp - 0.1f))
        return result
    }

    fun buildTable(result: Result) {
        println(
            "${'-'.enlarge(83, "-")}\n" +
                    "Test Duration (s) | Requests count | Requests per second | Ok requests | Ok percent\n" +
                    " ${result.diagnostics.duration.seconds.enlarge(17)}|" +
                    " ${result.diagnostics.requestCount.total.enlarge(15)}|" +
                    " ${result.diagnostics.requestsPerSecond.enlarge(20)}|" +
                    " ${result.diagnostics.requestCount.ok.enlarge(12)}|" +
                    " ${result.percentOk.enlarge(10)}\n" +
                    " ${'-'.enlarge(82, "-")}\n"
        )
    }

    fun Any.enlarge(length: Int, symbol: String = " "): String {
        var str = this.toString()
        if (str.length < length) {
            str = str.plus(symbol.repeat(length - str.length))
        }
        return str
    }

    @Test
    fun loadTestRequestsPerSeconds() {
        val result = loadGeneralTest(500, 5)
        Assertions.assertTrue(result.diagnostics.duration.seconds <= 5)
    }

    companion object {
        private val userId = UUID.randomUUID()
        private val projectId = UUID.randomUUID()
        private val loadTesterBuilder = JMeterBuilder.withUrl("http", "localhost", 8080)
    }
}