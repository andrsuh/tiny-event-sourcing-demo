package ru.quipy.subscribers.projections.handlers

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.events.projectManagment.project.AssigneeAddedEvent
import ru.quipy.events.projectManagment.project.ParticipantAddedEvent
import ru.quipy.events.projectManagment.project.ProjectCreatedEvent
import ru.quipy.events.projectManagment.project.StatusAddedEvent
import ru.quipy.events.projectManagment.project.StatusRemovedEvent
import ru.quipy.events.projectManagment.project.TaskChangedEvent
import ru.quipy.events.projectManagment.project.TaskCreatedEvent
import ru.quipy.repositories.ProjectRepository
import ru.quipy.repositories.StatusRepository
import ru.quipy.repositories.TaskRepository
import ru.quipy.states.projectManagment.ProjectAggregateState
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import ru.quipy.subscribers.projections.views.ProjectViewDomain
import ru.quipy.subscribers.projections.views.StatusViewDomain
import ru.quipy.subscribers.projections.views.TaskViewDomain

@Service
@AggregateSubscriber(
    aggregateClass = ProjectAggregate::class, subscriberName = "projection-project-event-subscriber"
)
class ProjectionProjectEventsSubscriber(
    @Autowired
    private val projectRepository: ProjectRepository,
    @Autowired
    private val statusRepository: StatusRepository,
    @Autowired
    private val taskRepository: TaskRepository
) {
    val logger: Logger = LoggerFactory.getLogger(ProjectionProjectEventsSubscriber::class.java)

    @SubscribeEvent
    fun projectCreatedSubscriber(event: ProjectCreatedEvent) {
        val statusView = StatusViewDomain.Status(StatusViewDomain.StatusId(event.projectId, ProjectAggregateState.DEFAULT_STATUS.id),
            ProjectAggregateState.DEFAULT_STATUS.name, ProjectAggregateState.DEFAULT_STATUS.colorCode)
        statusRepository.insert(statusView)

        val projectView = ProjectViewDomain.Project(event.projectId, event.projectName, event.creatorId)
        projectRepository.insert(projectView)

        logger.info(
            "Project VIEW created with id {}, name {} by user with id {}",
            event.projectId,
            event.projectName,
            event.creatorId,
        )
    }

    @SubscribeEvent
    fun statusAddedSubscriber(event: StatusAddedEvent) {
        val statusView = StatusViewDomain.Status(StatusViewDomain.StatusId(event.projectId, event.statusId), event.statusName, event.colorCode)
        statusRepository.insert(statusView)

        logger.info(
            "Status VIEW added with id {}, name {} to project with id {}",
            event.statusId,
            event.statusName,
            event.projectId,
        )
    }

    @SubscribeEvent
    fun statusRemovedSubscriber(event: StatusRemovedEvent) {
        statusRepository.deleteById(StatusViewDomain.StatusId(event.projectId, event.statusId))

        logger.info(
            "Status VIEW with id {} removed from project with id {}",
            event.statusId,
            event.projectId,
        )
    }

    @SubscribeEvent
    fun taskCreatedSubscriber(event: TaskCreatedEvent) {
        val taskView = TaskViewDomain.Task(TaskViewDomain.TaskId(event.projectId, event.taskId), event.taskName, ProjectAggregateState.DEFAULT_STATUS.id)
        taskRepository.insert(taskView)

        logger.info(
            "Task VIEW added with id {}, name {} to project with id {}",
            event.taskId,
            event.taskName,
            event.projectId,
        )
    }

    @SubscribeEvent
    fun taskChangedSubscriber(event: TaskChangedEvent) {
        val taskView = taskRepository.findById(TaskViewDomain.TaskId(event.projectId, event.taskId)).get()
        taskRepository.deleteById(TaskViewDomain.TaskId(event.projectId, event.taskId))

        if (event.newTaskName != null) {
            taskView.name = event.newTaskName

            logger.info(
                "Task VIEW with id {} in project with id {} renamed to {}",
                event.taskId,
                event.projectId,
                event.newTaskName,
            )
        }
        if (event.newStatusId != null) {
            taskView.statusId = event.newStatusId

            logger.info(
                "Task VIEW with id {} in project with id {} changed to status with id {}",
                event.taskId,
                event.projectId,
                event.newStatusId,
            )
        }

        taskRepository.insert(taskView)
    }

    @SubscribeEvent
    fun participantAddedSubscriber(event: ParticipantAddedEvent) {
        val projectView = projectRepository.findById(event.projectId).get()
        projectRepository.deleteById(event.projectId)
        projectView.participantIds.add(event.userId)
        projectRepository.insert(projectView)

        logger.info(
            "User with id {} added to project VIEW with id {}",
            event.userId,
            event.projectId,
        )
    }

    @SubscribeEvent
    fun assigneeAddedSubscriber(event: AssigneeAddedEvent) {
        val taskView = taskRepository.findById(TaskViewDomain.TaskId(event.projectId, event.taskId)).get()
        taskRepository.deleteById(TaskViewDomain.TaskId(event.projectId, event.taskId))
        taskView.assigneeIds.add(event.userId)
        taskRepository.insert(taskView)

        logger.info(
            "User with id {} assigned to task VIEW with id {} in project with id {}",
            event.userId,
            event.taskId,
            event.projectId,
        )
    }
}