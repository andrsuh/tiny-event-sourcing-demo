package ru.quipy
import kotlinx.coroutines.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)

class DemoApplicationTests  {
	companion object {
		val projectId = UUID.randomUUID()
		val creatorId = UUID.randomUUID()
		val user1Id = UUID.randomUUID()
		val user2Id = UUID.randomUUID()
		val taskId = UUID.randomUUID()
	}

	@Autowired
	private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
	@Autowired
	private lateinit var userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>

//	@Autowired
//	lateinit var mongoTemplate: MongoTemplate

	@Test
	@Order(1)
	fun createUsers() {
		userEsService.create { it.create(creatorId, "Ann", "Anny-waay", "aaaaaaaaaa") }
		userEsService.create { it.create(user1Id, "Tatiana", "tatia", "aaaaaaaaaa") }
		userEsService.create { it.create(user2Id, "Maria", "marianet", "aaaaaaaaaa") }

		Assertions.assertTrue(userEsService.getState(creatorId) != null)
		Assertions.assertTrue(userEsService.getState(user1Id) != null)
		Assertions.assertTrue(userEsService.getState(user2Id) != null)
	}
	@Test
	@Order(2)
	fun createProject() {
		val projectTitle = "Aaaa"

		runBlocking {
			val job1 = async { projectEsService.create { it.create(projectId, projectTitle, creatorId) } }
			val job2 = async { projectEsService.update(projectId) {
				it.addUser(user1Id)
			} }
			val job3 = async { projectEsService.update(projectId) { it.addTask(taskId,"Task 1") } }
			val job4 = async { projectEsService.update(projectId) {
				it.createTag(UUID.randomUUID(), "CREATED", "blue")
			} }

			job1.await()
			job2.await()
			job3.await()
			job4.await()
		}

		val state = projectEsService.getState(projectId)!!
		Assertions.assertEquals(projectId, state.getId())
		Assertions.assertEquals(projectTitle, state.projectTitle)
		Assertions.assertEquals(creatorId, state.creatorId)

		Assertions.assertTrue(state.projectMembers.contains(creatorId))
		Assertions.assertTrue(state.projectMembers.contains(user1Id))
		Assertions.assertEquals(state.projectMembers.count(), 2)

		Assertions.assertTrue(state.tasks.containsKey(taskId))
		Assertions.assertTrue(state.projectTags.count() == 1)
	}

	@Test
	fun  doubleAddUserToProject() {

		Assertions.assertThrows(Exception::class.java) {
			projectEsService.update(projectId) {
				it.addUser(user1Id)
			}
		}
	}

	@Test
	fun  createExistedTag() {
		val tagId = UUID.randomUUID()
		val tagName = "CREATED"
		val tagColor = "grey"

		Assertions.assertThrows(Exception::class.java) {
			projectEsService.update(projectId) {
				it.createTag(tagId, tagName, tagColor)
			}
		}
	}

	@Test
	fun  deleteTag() {
		val tagId = UUID.randomUUID()
		val tagName = "DONE"
		val tagColor = "green"

		runBlocking {
			val job1 = async { projectEsService.update(projectId) {
				it.createTag(tagId, tagName, tagColor)
			} }
			val job2 = async { projectEsService.update(projectId) {
				it.deleteTag(tagId)
			} }

			job1.await()
			job2.await()
		}

		val state = projectEsService.getState(projectId)!!

		state.tasks[taskId]?.tagsAssigned?.contains(tagId)?.let { Assertions.assertFalse(it) }
	}

	@Test
	fun  deleteAssignedTag() {
		val tagId = UUID.randomUUID()
		val tagName = "IN_PROGRESS"
		val tagColor = "grey"

		runBlocking {
			val job1 = async { projectEsService.update(projectId) {
				it.createTag(tagId, tagName, tagColor)
			} }
			val job2 = async { projectEsService.update(projectId) {
				it.assignTagToTask(tagId, taskId)
			} }

			job1.await()
			job2.await()
		}

		val state = projectEsService.getState(projectId)!!

		Assertions.assertTrue(state.projectTags.count() == 2)
		state.tasks[taskId]?.tagsAssigned?.contains(tagId)?.let { Assertions.assertTrue(it) }
		Assertions.assertThrows(Exception::class.java) {
			projectEsService.update(projectId) {
				it.deleteTag(tagId)
			}
		}
	}

	@Test
	fun  doubleAssignUserToTask() {

		projectEsService.update(projectId) {
				it.assignUserToTask(taskId, user1Id)
			}

		val state = projectEsService.getState(projectId)!!

		state.tasks[taskId]?.usersAssigned?.contains(user1Id).let {
			if (it != null) {
				Assertions.assertTrue(it)
			}
		}
		Assertions.assertThrows(Exception::class.java) {
			projectEsService.update(projectId) {
				it.assignUserToTask(taskId, user1Id)
			}
		}
	}

	@Test
	fun  assignUserNotFromProjectToTask() {

		Assertions.assertThrows(Exception::class.java) {
			projectEsService.update(projectId) {
				it.assignUserToTask(taskId, user2Id)
			}
		}
	}
}
