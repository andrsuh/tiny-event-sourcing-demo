package ru.quipy

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TagCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.api.UserCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.assignTagToTask
import ru.quipy.logic.assignUserToProject
import ru.quipy.logic.assignUserToTask
import ru.quipy.logic.changeColor
import ru.quipy.logic.changeName
import ru.quipy.logic.changeTaskTitle
import ru.quipy.logic.create
import ru.quipy.logic.createTag
import ru.quipy.logic.createTask
import ru.quipy.utils.*
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AggregatesTest {
    @Autowired
    private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

    @Autowired
    private lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

    @Test
    fun createUser_UserCreatedSuccess() = runBlocking {
        val firstUserId = UUID.randomUUID()
        val createdUser =
            userEsService.create {
                it.create(
                    firstUserId,
                    createTestUsername(1),
                    createTestNickname(1),
                    createTestPassword(1),
                )
            }
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

        userEsService.create {
            it.create(
                secondUserId,
                createTestUsername(1),
                createTestNickname(1),
                createTestPassword(1),
            )
        }

        val createdProject =
            projectEsService.create { it.create(firstProjectId, createTestProjectName(1), secondUserId) }
        val createdProject2 =
            projectEsService.create { it.create(secondProjectId, createTestProjectName(2), secondUserId) }
        val listId = listOf(createdProject.projectId, createdProject2.projectId)
        val received = projectEsService.getState(firstProjectId)

        Assertions.assertNotNull(received)
        Assertions.assertEquals(createdProject.title, received?.projectTitle)
        Assertions.assertEquals(listId.map { projectId -> projectEsService.getState(projectId) }.size, 2)
    }

    @Test
    fun addUserToProject_UserAddedSuccess() = runBlocking {
        val thirdUserId = UUID.randomUUID()
        val thirdProjectId = UUID.randomUUID()

        userEsService.create {
            it.create(
                thirdUserId,
                createTestUsername(1),
                createTestNickname(1),
                createTestPassword(1),
            )
        }
        projectEsService.create { it.create(thirdProjectId, createTestProjectName(3), thirdUserId) }

        val projectMember = projectEsService.update(thirdProjectId) {
            it.assignUserToProject(thirdUserId, createTestUsername(2), createTestNickname(2))
        }
        val received = projectEsService.getState(thirdProjectId)

        Assertions.assertNotNull(projectMember)
        Assertions.assertEquals(received?.projectMembers?.contains(thirdUserId), true)
    }

    @Test
    fun addDuplicateUserToProject_expectException() {
        val thirdUserId = UUID.randomUUID()
        val thirdProjectId = UUID.randomUUID()

        userEsService.create {
            it.create(
                thirdUserId,
                createTestUsername(1),
                createTestNickname(1),
                createTestPassword(1),
            )
        }
        projectEsService.create { it.create(thirdProjectId, createTestProjectName(1), thirdUserId) }

        projectEsService.update(thirdProjectId) {
            it.assignUserToProject(thirdUserId, createTestUsername(1), createTestNickname(1))
        }

        assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(thirdProjectId) {
                it.assignUserToProject(thirdUserId, createTestUsername(1), createTestNickname(1))
            }
        }
    }

    @Test
    fun createTagToProject_tagCreatedSuccess() = runBlocking {
        val fourthUserId = UUID.randomUUID()
        val fourthProjectId = UUID.randomUUID()

        userEsService.create {
            it.create(
                fourthUserId,
                createTestUsername(1),
                createTestNickname(1),
                createTestPassword(1)
            )
        }
        projectEsService.create { it.create(fourthProjectId, createTestProjectName(1), fourthUserId) }

        projectEsService.update(fourthProjectId) { it.createTag(createTestTagName(1), createTestTagColor(1)) }
        val received = projectEsService.getState(fourthProjectId)

        Assertions.assertNotNull(received)
        Assertions.assertEquals(received?.projectTags?.any { it.value.color == createTestTagColor(1) }, true)
        Assertions.assertEquals(received?.projectTags?.any { it.value.name == createTestTagName(1) }, true)
    }

    @Test
    fun createTagToProject_expectException() {
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()

        userEsService.create {
            it.create(
                userId,
                createTestUsername(1),
                createTestNickname(1),
                createTestPassword(1)
            )
        }
        projectEsService.create { it.create(projectId, createTestProjectName(1), userId) }

        projectEsService.update(projectId) { it.createTag(createTestTagName(1), createTestTagColor(1)) }
        assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(projectId) { it.createTag(createTestTagName(1), createTestTagColor(1)) }
        }
    }

    @Test
    fun changeProjectTag_expectException() {
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()

        userEsService.create {
            it.create(
                userId,
                createTestUsername(1),
                createTestNickname(1),
                createTestPassword(1),
            )
        }
        projectEsService.create { it.create(projectId, createTestProjectName(1), userId) }

        projectEsService.update(projectId) { it.createTag(createTestTagName(1), createTestTagColor(1)) }
        var received = projectEsService.getState(projectId)

        assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(projectId) {
                it.changeName(
                    createTestTagName(1),
                    received!!.projectTags.keys.first()
                )
            }
        }

        assertThrows(IllegalArgumentException::class.java) {
            projectEsService.update(projectId) {
                it.changeColor(
                    createTestTagColor(1),
                    received!!.projectTags.keys.first()
                )
            }
        }
    }

    @Test
    fun changeProjectTag_tagChangedSuccess() = runBlocking {
        val fourthUserId = UUID.randomUUID()
        val fourthProjectId = UUID.randomUUID()

        userEsService.create {
            it.create(
                fourthUserId,
                createTestUsername(1),
                createTestNickname(1),
                createTestPassword(1),
            )
        }
        projectEsService.create { it.create(fourthProjectId, createTestProjectName(1), fourthUserId) }

        projectEsService.update(fourthProjectId) { it.createTag(createTestTagName(1), createTestTagColor(1)) }
        var received = projectEsService.getState(fourthProjectId)

        projectEsService.update(fourthProjectId) {
            it.changeName(
                createTestTagName(2),
                received!!.projectTags.keys.first()
            )
        }
        projectEsService.update(fourthProjectId) {
            it.changeColor(
                createTestTagColor(2),
                received!!.projectTags.keys.first()
            )
        }

        received = projectEsService.getState(fourthProjectId)

        Assertions.assertEquals(received?.projectTags?.any { it.value.color == createTestTagColor(2) }, true)
        Assertions.assertEquals(received?.projectTags?.any { it.value.name == createTestTagName(2) }, true)
    }

    @Test
    fun createTask_TaskCreatedSuccess() {
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()

        userEsService.create { it.create(userId, createTestUsername(1), createTestNickname(1), createTestPassword(1)) }
        projectEsService.create { it.create(projectId, createTestProjectName(5), projectId) }

        projectEsService.update(projectId) { it.createTag(createTestTagName(1), createTestTagColor(1)) }

        val received = projectEsService.getState(projectId)!!

        val taskId = UUID.randomUUID()
        projectEsService.update(projectId) {
            it.createTask(
                taskId,
                createTestTaskTitle(1),
                projectId,
                received.projectTags.keys.single(),
                userId
            )
        }

        Assertions.assertEquals(projectEsService.getState(projectId)?.tasks?.get(taskId)?.taskTitile, createTestTaskTitle(1))
    }

    @Test
    fun changeTask_TaskNameChangedSuccess() {
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        userEsService.create { it.create(userId, createTestUsername(1), createTestNickname(1), createTestPassword(1)) }
        projectEsService.create { it.create(projectId, createTestProjectName(1), projectId) }
        projectEsService.update(projectId) { it.createTag(createTestTagName(1), createTestTagColor(1)) }

        val tagId = projectEsService.getState(projectId)!!.projectTags.keys.single()

        projectEsService.update(projectId) { it.createTask(taskId, createTestTaskTitle(1), projectId, tagId, userId) }
        projectEsService.update(projectId) { it.changeTaskTitle(taskId, createTestTaskTitle(2), projectId) }

        Assertions.assertEquals(projectEsService.getState(projectId)?.tasks?.get(taskId)?.taskTitile, createTestTaskTitle(2))
    }

    @Test
    fun changeTaskStatus_TaskStatusChangedSuccess() {
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        userEsService.create { it.create(userId, createTestUsername(1), createTestNickname(1), createTestPassword(1)) }
        projectEsService.create { it.create(projectId, createTestProjectName(1), projectId) }
        projectEsService.update(projectId) { it.createTag(createTestTagName(1), createTestTagColor(1)) }

        val tagId = projectEsService.getState(projectId)!!.projectTags.keys.single()

        projectEsService.update(projectId) { it.createTask(taskId, createTestTaskTitle(1), projectId, tagId, userId) }

        val tagCreatedEvent: TagCreatedEvent =
            projectEsService.update(projectId) { it.createTag(createTestTagName(2), createTestTagColor(2)) }

        projectEsService.update(projectId) { it.assignTagToTask(tagCreatedEvent.tagId, taskId) }

        Assertions.assertEquals(projectEsService.getState(projectId)?.tasks?.get(taskId)?.tagId, tagCreatedEvent.tagId)

        val secondUserId = UUID.randomUUID()
        userEsService.create { it.create(secondUserId, "Padme", "Padme", "SimplePassword") }
        projectEsService.update(projectId) { it.assignUserToTask(taskId, secondUserId, projectId) }
        Assertions.assertEquals(projectEsService.getState(projectId)?.tasks?.get(taskId)?.executors?.contains(secondUserId), true)
    }

    @Test
    fun assignUserToTask_TaskExecutorAssignedSuccess() {
        val userId = UUID.randomUUID()
        val projectId = UUID.randomUUID()
        val taskId = UUID.randomUUID()

        userEsService.create { it.create(userId, createTestUsername(1), createTestNickname(1), createTestPassword(1)) }
        projectEsService.create { it.create(projectId, createTestProjectName(1), projectId) }
        projectEsService.update(projectId) { it.createTag(createTestTagName(1), createTestTagColor(1)) }
        val tagId = projectEsService.getState(projectId)!!.projectTags.keys.single()

        projectEsService.update(projectId) { it.createTask(taskId, createTestTaskTitle(1), projectId, tagId, userId) }

        val user2: UserCreatedEvent = userEsService.create {
            it.create(
                UUID.randomUUID(),
                createTestUsername(2),
                createTestNickname(2),
                createTestPassword(2)
            )
        }
        projectEsService.update(projectId) { it.assignUserToTask(taskId, user2.userId, projectId) }

        Assertions.assertEquals(projectEsService.getState(projectId)?.tasks?.get(taskId)?.executors?.contains(user2.userId), true)
    }
}
