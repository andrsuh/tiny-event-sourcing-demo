package ru.quipy
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.ProjectAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class DemoApplicationTests {
	companion object {
		private val testId = UUID.randomUUID()
	}

	@Autowired
	private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>

	@Autowired
	lateinit var mongoTemplate: MongoTemplate

	@Test
	fun createProject() {
		val userId = UUID.randomUUID()
		val user1Id = UUID.randomUUID()
		val user2Id = UUID.randomUUID()
		val projectId = UUID.randomUUID()
		val projectTitle = "Aaaa"

		runBlocking {
			val job1 = async { projectEsService.create { it.create(projectId, projectTitle, userId) } }
			val job2 = async { projectEsService.update(projectId) {
				it.addUser(user1Id)
			} }
			val job3 = async { projectEsService.update(projectId) {
				it.addUser(user2Id)
			} }

			job1.await()
			job2.await()
			job3.await()
		}

		val state = projectEsService.getState(projectId)!!
		Assertions.assertEquals(projectId, state.getId())
		Assertions.assertEquals(projectTitle, state.projectTitle)
		Assertions.assertEquals(userId, state.creatorId)

		Assertions.assertTrue(state.projectMembers.contains(userId))
		Assertions.assertTrue(state.projectMembers.contains(user2Id))
		Assertions.assertTrue(state.projectMembers.contains(user1Id))
		Assertions.assertEquals(state.projectMembers.count(), 3)
	}

	@Test
	fun  createTask() {
		val userId = UUID.randomUUID()
		val projectId = UUID.randomUUID()
		val user1Id = UUID.randomUUID()
		val taskId = UUID.randomUUID()
		val tagId = UUID.randomUUID()
		val projectTitle = "Aaaa"
		val tagName = "CREATED"
		val tagColor = "blue"

		runBlocking {
			val job1 = async { projectEsService.create { it.create(projectId, projectTitle, userId) } }
			val job2 = async { projectEsService.update(projectId) { it.addTask(taskId,"Task 1") } }
			val job3 = async { projectEsService.update(projectId) {
				it.addUser(user1Id)
			} }
			val job4 = async { projectEsService.update(projectId) {
				it.createTag(tagId, tagName, tagColor)
			} }
			val job5 = async { projectEsService.update(projectId) {
				it.assignTagToTask(tagId, taskId)
			} }
			val job6 = async { projectEsService.update(projectId) {
				it.assignUserToTask(taskId, user1Id)
			}}

			job1.await()
			job2.await()
			job3.await()
			job4.await()
			job5.await()
			job6.await()
		}

		val state = projectEsService.getState(projectId)!!

		state.tasks[taskId]?.tagsAssigned?.contains(tagId)?.let { Assertions.assertTrue(it) }
		Assertions.assertTrue(state.projectMembers.contains(user1Id))
		state.tasks[taskId]?.usersAssigned?.contains(user1Id).let {
			if (it != null) {
				Assertions.assertTrue(it)
			}
		}
	}
}
