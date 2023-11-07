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
import ru.quipy.api.StatusCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class LoadTests {
    private val testId = UUID.fromString("ba4463d8-4332-4b4c-ac5e-2763bb83ada4")
    private val userId = UUID.fromString("43b971d0-253f-49d8-87db-10d15c552eae")
    private val taskName = "Name-b8e468ec-995a-4b52-ac14-468c830f8dd0"
    private val statusColor = "4287f5"
    private val testProjectName = "testProjectName$541(*@#&91459"

    @Autowired
    private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    private lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun init() {
        mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(testId)), "aggregate-project")
        mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(userId)), "aggregate-user")
        mongoTemplate.remove(Query.query(Criteria.where("snapshot.projectId").`is`(testId)), "snapshots")

        userEsService.create {
            it.create(userId, "Aboba", "Abobvich", "hashhhhhhhh")
        }

        projectEsService.create {
            it.create(testId, testProjectName, userId)
        }
    }

    val loadTesterBuilder = JMeterBuilder.withUrl("http", "localhost", 8080)
    // *numThreads* - Количество пользователей
    // *Ramp-Up Period* - указывает JMeter, какую задержку перед запуском следующего пользователя нужно сделать.
    //      Например, если у нас 100 пользователей и период Ramp-Up 100 секунд,
    //      то задержка между запуском пользователей составит 1 секунду (100 секунд /100 пользователей)
    // numThreads / RumpUp = количество запросов в секунду

    fun formRelativeUrlForLT(): String {
        val oneStatusEvent: StatusCreatedEvent = projectEsService.update(testId) {
            it.addStatus("1", statusColor)
        }

        val createdTaskEvent: TaskCreatedEvent = projectEsService.update(testId) {
            it.addTask(taskName)
        }

        return "/projects/${testId}/status/assign/${createdTaskEvent.taskId}/${oneStatusEvent.statusId}"
    }


    fun loadGeneralTest(numThreads: Int, rampUp: Int): Result {
        val loadTester: LoadTester = loadTesterBuilder
            .withNumThreads(numThreads) // Количество пользователей
            .withRampUp(rampUp)
            .build()

        val result = loadTester.run(
            listOf(
                Request.put(formRelativeUrlForLT())
                    .withHeader("Accept", "*/*")
            )
        )
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
    fun loadTest20RequestsPerSeconds() {
        val result = loadGeneralTest(100, 5)
        Assertions.assertTrue(result.diagnostics.duration.seconds <= 5)
    }

    @Test
    fun loadTest50RequestsPerSeconds() {
        val result = loadGeneralTest(250, 5)
        Assertions.assertTrue(result.diagnostics.duration.seconds <= 5)
    }

    @Test
    fun loadTest100RequestsPerSeconds() {
        val result = loadGeneralTest(500, 5)
        Assertions.assertTrue(result.diagnostics.duration.seconds <= 5)
    }


    /**
     FAIL
     -----------------------------------------------------------------------------------
     Test Duration (s) | Requests count | Requests per second | Ok requests | Ok percent
     4                | 650            | 131.1276982045592   | 626         | 96.3076923076923
     ----------------------------------------------------------------------------------
     */
    @Test
    fun loadTest130RequestsPerSeconds() {
        val result = loadGeneralTest(650, 5)
        Assertions.assertTrue(result.diagnostics.duration.seconds <= 5)
    }


    /**
    fail
    -----------------------------------------------------------------------------------
    Test Duration (s) | Requests count | Requests per second | Ok requests | Ok percent
    62               | 2500           | 39.91506075072246   | 1565        | 62.6
    ----------------------------------------------------------------------------------
     65 per second
     */
    @Test
    fun loadTest650Requests10() {
        val result = loadGeneralTest(650, 10)
        Assertions.assertTrue(result.diagnostics.duration.seconds <= 10)
    }


    /**
    OK?
    -----------------------------------------------------------------------------------
    Test Duration (s) | Requests count | Requests per second | Ok requests | Ok percent
    6                | 650            | 92.93680297397769   | 649         | 99.84615384615385
    ----------------------------------------------------------------------------------
    */
    @Test
    fun loadTest650Requests7() {
        val result = loadGeneralTest(650, 7)
        Assertions.assertTrue(result.diagnostics.duration.seconds <= 7)
    }

}
