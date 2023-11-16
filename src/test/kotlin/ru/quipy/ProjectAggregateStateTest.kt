package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import ru.quipy.api.ProjectAggregate
import ru.quipy.logic.ProjectAggregateState
import org.springframework.test.annotation.DirtiesContext
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.create
import java.math.BigDecimal
import java.util.*
import org.junit.*
import org.loadtest4j.LoadTester
import org.loadtest4j.Request
import org.loadtest4j.drivers.gatling.GatlingBuilder
import java.time.Duration

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProjectAggregateStateTest {
    companion object {
        private val testId = UUID.randomUUID()
        private val userId = UUID.randomUUID()
    }

    @Autowired
    private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Test
    fun loadGeneralTest() {
        val loadTester: LoadTester = GatlingBuilder
            .withUrl("http://localhost:8080")
            .withDuration(Duration.ofSeconds(60))
            .withUsersPerSecond(20)
            .build();
//            .withNumThreads(numThreads) // Количество пользователей
//            .withRampUp(rampUp)
//            .build()

        val result = loadTester.run(
            listOf(
                Request.get("projects/1b4087b6-0119-4451-b007-047ff7122eaa")
                    .withHeader("Accept", "*/*")
            )
        )
        println(result)
        Assertions.assertTrue(result.percentOk > 99.99f)
//        Assertions.assertTrue(result.diagnostics.requestsPerSecond >= )
    }
}