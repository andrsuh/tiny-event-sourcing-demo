package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.UserAggregateState
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class TaskInfoProjection (
    private val taskInfoRepository: TaskInfoRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager,
    private val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>,
    private val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>
    ){
        private val logger = LoggerFactory.getLogger(TaskInfoProjection::class.java)

        @PostConstruct
        fun init() {
            subscriptionsManager.createSubscriber(ProjectAggregate::class, "taskInformation::task-information-cache") {
                `when`(TaskCreatedEvent::class) { event ->
                    val bdTaskInfo = withContext(Dispatchers.IO) {
                        taskInfoRepository.findById(event.taskId)
                    }
                    var taskInfo = TaskInfo(event.taskId, event.taskName)
                    if (!bdTaskInfo.isEmpty)
                        taskInfo = bdTaskInfo.get()
                    withContext(Dispatchers.IO) {
                        taskInfoRepository.save(taskInfo)
                    }
                    logger.info("Update task information projection, create task ${event.taskId}")
                }
                `when`(TagAssignedToTaskEvent::class) { event ->
                    val bdTaskInfo = withContext(Dispatchers.IO) {
                        taskInfoRepository.findById(event.taskId)
                    }
                    val project = projectEsService.getState(event.projectId)
                    var taskName = ""
                    if (project != null) {
                        val task = project.tasks[event.taskId]
                        if (task != null)
                            taskName = task.name
                    }
                    var taskInfo = TaskInfo(event.taskId, taskName)
                    if (!bdTaskInfo.isEmpty)
                        taskInfo = bdTaskInfo.get()
                    if (project != null) {
                        val tag = project.projectTags[event.tagId]
                        if (tag != null) {
                            taskInfo.tag[event.tagId] = TagInfo(event.tagId, tag.name, tag.color)
                        }
                    }
                    withContext(Dispatchers.IO) {
                        taskInfoRepository.save(taskInfo)
                    }
                    logger.info("Update task information projection, assign tag to task ${event.tagId}-${event.taskId}")
                }
                `when`(TaskRenamedEvent::class) { event ->
                    val bdTaskInfo = withContext(Dispatchers.IO) {
                        taskInfoRepository.findById(event.taskId)
                    }
                    var taskInfo = TaskInfo(event.taskId, event.taskName)
                    if (!bdTaskInfo.isEmpty)
                        taskInfo = bdTaskInfo.get()
                    taskInfo.name = event.taskName
                    withContext(Dispatchers.IO) {
                        taskInfoRepository.save(taskInfo)
                    }
                    logger.info("Update task information projection, task name change ${event.taskId}-${event.taskName}")
                }
                `when`(UserAssignedToTaskEvent::class) { event ->
                    val bdTaskInfo = withContext(Dispatchers.IO) {
                        taskInfoRepository.findById(event.taskId)
                    }
                    val project = projectEsService.getState(event.projectId)
                    var taskName = ""
                    if (project != null) {
                        val task = project.tasks[event.taskId]
                        if (task != null)
                            taskName = task.name
                    }
                    var taskInfo = TaskInfo(event.taskId, taskName)
                    if (!bdTaskInfo.isEmpty)
                        taskInfo = bdTaskInfo.get()

                    val user = userEsService.getState(event.userId)
                    if (user != null) {
                        taskInfo.performers[event.userId] = TaskPerformer(event.userId, user.name)
                    }
                    withContext(Dispatchers.IO) {
                        taskInfoRepository.save(taskInfo)
                    }
                    logger.info("Update task information projection, assign user to task ${event.userId}-${event.taskId}")
                }
            }
        }
}

@Document("task-information-projection")
data class TaskInfo(
        @Id
        var taskId: UUID,
        var name: String,
        var tag: MutableMap<UUID, TagInfo> = mutableMapOf(),
        val performers: MutableMap<UUID, TaskPerformer> = mutableMapOf()
)

data class TagInfo(
        val statusId: UUID,
        var name: String,
        var color: String
)

data class TaskPerformer(
        val statusId: UUID,
        var name: String
)

@Repository
interface TaskInfoRepository: MongoRepository<TaskInfo, UUID>