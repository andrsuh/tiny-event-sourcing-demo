package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.ProjectAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.create
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProjectAggregateStateTest {
    companion object {
        private val testId = UUID.randomUUID()
        private val userId = UUID.randomUUID()
        private val testProjectName = "testProjectName$541(*@#&91459"
    }

    @Autowired
    private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @BeforeEach
    fun init() {
        mongoTemplate.remove(Query.query(Criteria.where("aggregateId").`is`(testId)), "accounts")
        mongoTemplate.remove(Query.query(Criteria.where("_id").`is`(testId)), "snapshots")
        println(mongoTemplate.findAll(ProjectAggregate::class.java))
    }

    @Test
    fun createProject() {
        projectEsService.create {
            it.create(testId, testProjectName, userId.toString());
        }
        val state = projectEsService.getState(testId)
        Assertions.assertEquals(testId, state?.getId());
    }
}
