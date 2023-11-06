package ru.quipy

import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@Import({EventSourcingLibConfiguration::class})
class ProjectAggregateStateTest {

    private val testId = UUID.fromString("ba4463d8-4332-4b4c-ac5e-2763bb83ada4")
    private val userId = UUID.fromString("43b971d0-253f-49d8-87db-10d15c552eae")

    private val taskId = UUID.fromString("b8e468ec-995a-4b52-ac14-468c830f8dd0")
    private val taskName = "Name-b8e468ec-995a-4b52-ac14-468c830f8dd0"
    private val taskNewName = "NewName-b8e468ec-995a-4b52-ac14-468c830f8dd0"

    private val statusId = UUID.fromString("7074d67c-1831-44e7-a29e-a4210e69eb39")
    private val statusName = "7074d67c-1831-44e7-a29e-a4210e69eb39Name"
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
            it.create(testId, testProjectName, userId);
        }

        Thread.sleep(1000)
    }


    @Test
    fun createProject() {
        val state = projectEsService.getState(testId)
        Assertions.assertEquals(testId, state?.getId());
        Assertions.assertNotNull(state)
    }

    @Test
    fun createStatus() {
        val createdStatusEvent: StatusCreatedEvent = projectEsService.update(testId) {
            it.addStatus(statusName, statusColor)
        }
        var state = projectEsService.getState(testId)
        Assertions.assertNotNull(state)
        Assertions.assertEquals(state!!.projectStatuses.count(), 2)
        projectEsService.update(testId) {
            it.deleteStatus(testId, createdStatusEvent.statusId)
        }
        state = projectEsService.getState(testId)
        Assertions.assertEquals(state!!.projectStatuses.count(), 1)
    }

    @Test
    fun createTask() {
        val createdStatusEvent: StatusCreatedEvent = projectEsService.update(testId) {
            it.addStatus(statusName, statusColor)
        }

        val createdTaskEvent: TaskCreatedEvent = projectEsService.update(testId) {
            it.addTask(taskName)
        }
        var state: ProjectAggregateState = projectEsService.getState(testId) ?: throw IllegalArgumentException()
        Assertions.assertNotNull(state)
        Assertions.assertEquals(state.tasks.count(), 1)

        val renamedTaskEvent: TaskRenamedEvent = projectEsService.update(testId) {
            it.renameTask(testId, createdTaskEvent.taskId, taskNewName)
        }
        state = projectEsService.getState(testId) ?: throw IllegalArgumentException()
        Assertions.assertNotNull(state)
        Assertions.assertNotNull(state.tasks[createdTaskEvent.taskId])
        Assertions.assertEquals(state.tasks[createdTaskEvent.taskId]!!.name, taskNewName)
    }

    @Test
    fun deleteUsingStatus() {
        val createdStatusEvent: StatusCreatedEvent = projectEsService.update(testId) {
            it.addStatus(statusName, statusColor)
        }
        val createdTaskEvent: TaskCreatedEvent = projectEsService.update(testId) {
            it.addTask(taskName)
        }
        projectEsService.update(testId) {
            it.assignStatus(testId, createdTaskEvent.taskId, createdStatusEvent.statusId)
        }
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(testId) {
                it.deleteStatus(testId, createdStatusEvent.statusId)
            }
        }
    }

    @Test
    fun addAddedUserThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(testId) {
                it.addUser(testId, userId)
            }
        }
    }

    // из соурсов видно, что делается максимум
    // EventSourcingProperties.spinLockMaxAttempts, default 25
    @Test
    fun testOptimisticLockExceptionLessThan25() {
        val scope = CoroutineScope(Job())

        val oneStatusEvent: StatusCreatedEvent = projectEsService.update(testId) {
            it.addStatus("1", statusColor)
        }
        val twoStatusEvent: StatusCreatedEvent = projectEsService.update(testId) {
            it.addStatus("2", statusColor)
        }
        val createdTaskEvent: TaskCreatedEvent = projectEsService.update(testId) {
            it.addTask(taskName)
        }

        runBlocking {
            val jobs = List(24) {
                scope.async {
                    try {
                        projectEsService.update(testId) {
                            it.assignStatus(testId, createdTaskEvent.taskId, oneStatusEvent.statusId)
                        }
                    } catch (_: IllegalArgumentException) {
                    }
                }
            }
            jobs.awaitAll()
        }

        projectEsService.update(testId) {
            it.assignStatus(testId, createdTaskEvent.taskId, twoStatusEvent.statusId)
        }
        val state = projectEsService.getState(testId)
        Assertions.assertNotNull(state)
        println(state?.tasks?.get(twoStatusEvent.statusId))
        Assertions.assertEquals(twoStatusEvent.statusId, state?.tasks?.get(createdTaskEvent.taskId)?.status)
    }


    @Test
    fun testOptimisticLockExceptionMoreThan25() {
        val scope = CoroutineScope(Job())

        val oneStatusEvent: StatusCreatedEvent = projectEsService.update(testId) {
            it.addStatus("1", statusColor)
        }
        val twoStatusEvent: StatusCreatedEvent = projectEsService.update(testId) {
            it.addStatus("2", statusColor)
        }
        val createdTaskEvent: TaskCreatedEvent = projectEsService.update(testId) {
            it.addTask(taskName)
        }

        val exceptionCounter = AtomicInteger(0)
        runBlocking {
            val jobs = List(1000) {
                scope.async {
                    try {
                        projectEsService.update(testId) {
                            it.assignStatus(testId, createdTaskEvent.taskId, oneStatusEvent.statusId)
                        }
                        val state = projectEsService.getState(testId)
                        Assertions.assertEquals(oneStatusEvent.statusId,
                            state?.tasks?.get(createdTaskEvent.taskId)?.status)
                    } catch (_: IllegalStateException) {
                        println("EXCEPTION THROW")
                        if (exceptionCounter.get() == 1) {
                            try {
                                projectEsService.update(testId) {
                                    it.assignStatus(testId, createdTaskEvent.taskId, twoStatusEvent.statusId)
                                }
                            } catch (_: IllegalStateException) {
                                return@async
                            }
                        }
                        exceptionCounter.incrementAndGet()
                    }
                }
            }
            jobs.awaitAll()
        }

        val state = projectEsService.getState(testId)
        Assertions.assertNotNull(state)
        println(state?.tasks?.get(createdTaskEvent.taskId)?.status == twoStatusEvent.statusId)
        Assertions.assertEquals(oneStatusEvent.statusId, state?.tasks?.get(createdTaskEvent.taskId)?.status)
        Assertions.assertNotEquals(twoStatusEvent.statusId, state?.tasks?.get(createdTaskEvent.taskId)?.status)
    }
}

