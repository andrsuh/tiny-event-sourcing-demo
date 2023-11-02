package ru.quipy

import kotlinx.coroutines.*
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
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
//@Import({EventSourcingLibConfiguration::class})
class ProjectAggregateStateTest {
    companion object {
        private val testId = UUID.fromString("ba4463d8-4332-4b4c-ac5e-2763bb83ada4")
        private val userId = UUID.fromString("43b971d0-253f-49d8-87db-10d15c552eae")

        private val taskId = UUID.fromString("b8e468ec-995a-4b52-ac14-468c830f8dd0")
        private val taskName = "Name-b8e468ec-995a-4b52-ac14-468c830f8dd0"
        private val taskNewName = "NewName-b8e468ec-995a-4b52-ac14-468c830f8dd0"

        private val statusId = UUID.fromString("7074d67c-1831-44e7-a29e-a4210e69eb39")
        private val statusName = "7074d67c-1831-44e7-a29e-a4210e69eb39Name"
        private val statusColor = "4287f5"

        private val testProjectName = "testProjectName$541(*@#&91459"
    }

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

        userEsService.create {
            it.create(userId, "Aboba", "Abobvich", "hashhhhhhhh")
        }

        projectEsService.create {
            it.create(testId, testProjectName, userId);
        }

        val state = projectEsService.getState(testId)
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
        val state = projectEsService.getState(testId)
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


    // короче тут выбрасывается ошика при оптимитичной блокировке, хотя мы просто меняем статусы у задачи
    @Test
    fun asyncStatusChange() {
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

        // TODO: это плохо или нет, optimisticLock exception?
        Assertions.assertThrows(IllegalStateException::class.java) {
            runBlocking {
                val jobs = List(200) {
                    scope.async {
                        delay(1000)
                        projectEsService.update(testId) {
                            it.assignStatus(testId, createdTaskEvent.taskId, oneStatusEvent.statusId)
                        }
                        delay(1000)
                        projectEsService.update(testId) {
                            it.assignStatus(testId, createdTaskEvent.taskId, twoStatusEvent.statusId)
                        }
                    }
                }

                jobs.awaitAll()
            }
        }
    }

    @Test
    fun asyncAddStatusesAndTryDeleteWhileStatusChanges() {
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
            val jobs = List(10) {
                scope.async {
                    try {
                        delay(100)
                        projectEsService.update(testId) {
                            it.assignStatus(testId, createdTaskEvent.taskId, oneStatusEvent.statusId)
                        }
                        delay(100)
                        projectEsService.update(testId) {
                            it.assignStatus(testId, createdTaskEvent.taskId, twoStatusEvent.statusId)
                        }
                        delay(100)
                        projectEsService.update(testId) {
                            it.deleteStatus(testId, oneStatusEvent.statusId)
                        }
                    } catch (_: IllegalArgumentException) {
                    }
                }
            }
            jobs.awaitAll()
        }

        val state = projectEsService.getState(testId)
        Assertions.assertNull(state?.projectStatuses?.get(oneStatusEvent.statusId))
        Assertions.assertNotNull(state?.tasks?.get(twoStatusEvent.statusId))

    }

    @Test
    fun blablabla() {
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

        val taskNameFirst = "First"
        val taskNameSecond = "Second"

        val atomic: AtomicInteger = AtomicInteger(-1)
        runBlocking {
            val jobs = List(1000) {
                scope.async {
                    try {
//                        delay(100)
//                        projectEsService.update(testId) {
//                            it.assignStatus(testId, createdTaskEvent.taskId, oneStatusEvent.statusId)
//                        }
//                        delay(100)
//                        projectEsService.update(testId) {
//                            it.assignStatus(testId, createdTaskEvent.taskId, twoStatusEvent.statusId)
//                        }
//                        delay(100)
                        val ev = projectEsService.update(testId) {
                            it.renameTask(testId, createdTaskEvent.taskId, taskNameFirst + atomic.get())
                        }
                        atomic.incrementAndGet()
//                        println(ev == null);
                    } catch (_: IllegalArgumentException) {
                    }
                }
            }
            jobs.awaitAll()
        }

        val state = projectEsService.getState(testId)
        Assertions.assertNotNull(state)
        val taskName = state?.tasks?.get(createdTaskEvent.taskId)?.name
        println(taskName)
        Assertions.assertTrue(taskName == "Second9" || taskName == "First9")
//        Assertions.assertNull(state?.projectStatuses?.get(oneStatusEvent.statusId))
//        Assertions.assertNotNull(state?.tasks?.get(twoStatusEvent.statusId))

    }
}

