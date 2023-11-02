package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.logic.assignUserToProject
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

    private val firstUserId =  UUID.randomUUID()
    private val secondUserId =  UUID.randomUUID()
    private val firstProjectId =  UUID.randomUUID()
    private val secondProjectId =  UUID.randomUUID()
    private val thirdProjectId =  UUID.randomUUID()
    @Test
    fun createUser() {
        val createdUser = userEsService.create { it.create(firstUserId, "Zmushko", "Andrew", "SimplePassword")}
        val createdUser2 = userEsService.create { it.create(secondUserId, "Zinchik", "Nagibator228", "SimplePassword2")}
        val receivedUser = userEsService.getState(firstUserId)
        Assertions.assertNotNull(receivedUser)
        Assertions.assertEquals(createdUser.username, receivedUser?.username)
        Assertions.assertEquals(createdUser.nickname, receivedUser?.nickname)
        Assertions.assertEquals(createdUser.password, receivedUser?.password)
    }

    @Test
    fun createProjectLifecycle() {
        val created = projectEsService.create { it.create(firstProjectId, "TestTitleOne", firstUserId)}
        val created2 = projectEsService.create { it.create(firstProjectId, "TestTitleTwo", secondProjectId)}
        val created3 = projectEsService.create { it.create(firstProjectId, "TestTitleThree", secondProjectId)}
        val listId = emptyList<UUID>().plus(created).plus(created2).plus(created3)
        val received = projectEsService.getState(firstProjectId)
        Assertions.assertNotNull(received)
        Assertions.assertEquals(created.title, received?.projectTitle)
        val listReceived = listId.map { projectId -> projectEsService.getState(projectId as UUID) }
        Assertions.assertEquals(listReceived.size, 3)
    }

    @Test
    fun createUserToProject() {
        val projectMember = projectEsService.update(thirdProjectId) {
            it.assignUserToProject(secondUserId, "Zinchik", "Nagibator228",) }
        val received = projectEsService.getState(thirdProjectId)
        Assertions.assertNotNull(projectMember)
        Assertions.assertEquals(received?.projectMembers?.contains(thirdProjectId), true)
    }

    @Test
    fun createTagToProject() {
        val createdTag = projectEsService.update(secondProjectId) { it.createTag("TestTag", "White, Blue, Red") }
        val received = projectEsService.getState(secondProjectId)
        Assertions.assertNotNull(createdTag)
        Assertions.assertEquals(received?.projectTags?.any { it.value.color == "White, Blue, Red" }, true)
    }
}
