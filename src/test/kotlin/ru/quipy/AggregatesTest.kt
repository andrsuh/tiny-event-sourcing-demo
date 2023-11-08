package ru.quipy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TagCreatedEvent
import ru.quipy.api.TaskAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.TaskAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.assignUserToProject
import ru.quipy.logic.assignUserToTask
import ru.quipy.logic.changeColor
import ru.quipy.logic.changeName
import ru.quipy.logic.changeStatus
import ru.quipy.logic.changeTitle
import ru.quipy.logic.create
import ru.quipy.logic.createTag
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AggregatesTest {
    @Autowired
    private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    private lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Autowired
    private lateinit var taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>

    @Test
    fun createUser() {
        val firstUserId = UUID.randomUUID()
        val createdUser = userEsService.create { it.create(firstUserId, "test", "test", "SimplePassword") }
        val receivedUser = userEsService.getState(firstUserId)
        Assertions.assertNotNull(receivedUser)
        Assertions.assertEquals(createdUser.username, receivedUser?.username)
        Assertions.assertEquals(createdUser.nickname, receivedUser?.nickname)
        Assertions.assertEquals(createdUser.password, receivedUser?.password)
    }

    @Test
    fun createProjectLifecycle() = runBlocking {
        val secondUserId = UUID.randomUUID()
        val firstProjectId = UUID.randomUUID()
        val secondProjectId = UUID.randomUUID()

        userEsService.create { it.create(secondUserId, "Zinchik", "Nagibator228", "SimplePassword2") }

        val createdProject = projectEsService.create { it.create(firstProjectId, "TestTitleOne", secondUserId) }
        val createdProject2 = projectEsService.create { it.create(secondProjectId, "TestTitleTwo", secondUserId) }
        val listId = emptyList<UUID>().plus(createdProject.projectId).plus(createdProject2.projectId)
        val received = projectEsService.getState(firstProjectId)

        Assertions.assertNotNull(received)
        Assertions.assertEquals(createdProject.title, received?.projectTitle)

        val listReceived = listId.map { projectId -> projectEsService.getState(projectId) }

        Assertions.assertEquals(listReceived.size, 2)
    }

    @Test
    fun addUserToProject() = runBlocking {
        val thirdUserId = UUID.randomUUID()
        userEsService.create { it.create(thirdUserId, "Lion", "King", "SimplePassword3") }

        val thirdProjectId = UUID.randomUUID()
        projectEsService.create { it.create(thirdProjectId, "TestTitleThree", thirdUserId) }

        val projectMember = projectEsService.update(thirdProjectId) {
            it.assignUserToProject(thirdUserId, "Lion", "King")
        }
        val received = projectEsService.getState(thirdProjectId)

        Assertions.assertNotNull(projectMember)
        Assertions.assertEquals(received?.projectMembers?.contains(thirdUserId), true)
    }

    @Test
    fun addDublicateUserToProject() {
        val thirdUserId = UUID.randomUUID()
        userEsService.create { it.create(thirdUserId, "Lion", "King", "SimplePassword3") }

        val thirdProjectId = UUID.randomUUID()
        projectEsService.create { it.create(thirdProjectId, "TestTitleThree", thirdUserId) }

        projectEsService.update(thirdProjectId) {
            it.assignUserToProject(thirdUserId, "Lion", "King")
        }

        assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(thirdProjectId) {
                it.assignUserToProject(thirdUserId, "Lion", "King")
            }
        }
    }

    @Test
    fun createTagToProject() = runBlocking {
        val fourthUserId = UUID.randomUUID()
        userEsService.create { it.create(fourthUserId, "Luke", "blbla", "SimplePassword4") }
        val fourthProjectId = UUID.randomUUID()
        projectEsService.create { it.create(fourthProjectId, "TestTitleFour", fourthUserId) }

        projectEsService.update(fourthProjectId) { it.createTag("TestTag", "White, Blue, Red") }
        val received = projectEsService.getState(fourthProjectId)
        Assertions.assertNotNull(received)
        Assertions.assertEquals(received?.projectTags?.any { it.value.color == "White, Blue, Red" }, true)
        Assertions.assertEquals(received?.projectTags?.any { it.value.name == "TestTag" }, true)

        if (received != null) {
            projectEsService.update(fourthProjectId) { it.changeName("NewName", received.projectTags.keys.first()) }
            projectEsService.update(fourthProjectId) { it.changeColor("NewColor", received.projectTags.keys.first()) }
        }
        val newReceived = projectEsService.getState(fourthProjectId)
        Assertions.assertEquals(newReceived?.projectTags?.any { it.value.color == "NewColor" }, true)
        Assertions.assertEquals(newReceived?.projectTags?.any { it.value.name == "NewName" }, true)
    }

    @Test
    fun createTaskAndCheckCorrectChangedNameAtTask() {
        val userId = UUID.randomUUID()
        userEsService.create { it.create(userId, "Leia", "Leia", "SimplePassword") }
        val projectId = UUID.randomUUID()
        projectEsService.create { it.create(projectId, "TestTitleFifth", projectId) }

        projectEsService.update(projectId) { it.createTag("TestTag", "White, Blue, Red") }
        val received = projectEsService.getState(projectId)
        if (received != null) {
            val taskId = UUID.randomUUID()
            var tagId = received.projectTags.keys.first()
            taskEsService.create { it.create(taskId, "TestTaskTitle", projectId, tagId, userId) }
            var receivedTask = taskEsService.getState(taskId)
            Assertions.assertEquals(receivedTask?.taskTitle, "TestTaskTitle")

            taskEsService.update(taskId) { it.changeTitle(taskId, "newTitle", projectId) }
            receivedTask = taskEsService.getState(taskId)
            Assertions.assertEquals(receivedTask?.taskTitle, "newTitle")

            projectEsService.update(projectId) { it.createTag("NewName", "NewColor") }
            var receivedProject = projectEsService.getState(projectId)
            tagId = received.projectTags.keys.first()
            taskEsService.update(taskId) { it.changeStatus(taskId, tagId, projectId) }
            Assertions.assertEquals(taskEsService.getState(taskId)?.tagId, tagId)

            val secondUserId = UUID.randomUUID()
            userEsService.create { it.create(secondUserId, "Padme", "Padme", "SimplePassword") }
            taskEsService.update(taskId) { it.assignUserToTask(taskId, secondUserId, projectId) }
            Assertions.assertEquals(taskEsService.getState(taskId)?.executors?.contains(secondUserId), true)
        }
    }

    @Test
    fun changeProjectStatus() {
        val scope = CoroutineScope(Job())
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        var userCreatedEvent: UserCreatedEvent = userEsService.create { it.create(userId, "Aiven", "sputnik6109", "gagarin") }
        var projectCreatedEvent: ProjectCreatedEvent = projectEsService.create { it.create(projectId, "Project X", userId) }
        var tagCreatedEvent: TagCreatedEvent = projectEsService.update(projectId) { it.createTag("TestTag", "White") }

        runBlocking {
            val jobs = List(100000) {
                scope.async {
                    delay(1000)
                    projectEsService.update(projectId) {
                        it.changeName("Eagle" + UUID.randomUUID(), tagCreatedEvent.tagId)
                    }
                    delay(1000)
                    var i = 0;
                    while (i < 1000) {
                        projectEsService.create { it.create(UUID.randomUUID(), "Project X" + UUID.randomUUID(), userId) }
                        i++
                    }
                    delay(1000)
                    projectEsService.update(projectId) {
                        it.changeColor("Blue" + UUID.randomUUID(), tagCreatedEvent.tagId)
                    }
                }
            }
            jobs.awaitAll()
        }
    }
}
