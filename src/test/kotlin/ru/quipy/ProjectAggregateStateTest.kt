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
    fun createProject() {
        projectEsService.create { it.create(id = testId, title = "Aaaa", creatorId = userId) }
        val state = projectEsService.getState(testId)!!
        Assertions.assertEquals(testId, state.getId())
    }
}