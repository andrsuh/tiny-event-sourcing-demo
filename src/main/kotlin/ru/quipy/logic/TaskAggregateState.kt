package ru.quipy.logic

import org.springframework.beans.factory.annotation.Autowired
import ru.quipy.api.ExecutorAssignedToTaskEvent
import ru.quipy.api.ExecutorRetractedFromTaskEvent
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.TaskAggregate
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.api.TaskStatusAssignedToTaskEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class TaskAggregateState() : AggregateState<UUID, TaskAggregate> {

    // TODO think if it`s OK to inject this service
    @Autowired
    private lateinit var projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>


    private lateinit var taskId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectId: UUID
    lateinit var name: String
    lateinit var creatorId: UUID
    lateinit var taskStatusId: UUID

    val executorIds = mutableSetOf<UUID>()

    override fun getId(): UUID = taskId

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        taskId = event.taskId
        projectId = event.projectId
        name = event.taskName
        creatorId = event.creatorId

        projectEsService.getState(projectId)!!
            .taskStatuses
            .values
            .first { it.name == TaskStatusEntity.DEFAULT_TASK_STATUS_NAME }
            .also {
                taskStatusId = it.id
            }

        createdAt = event.createdAt
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusAssignedToTaskEventApply(event: TaskStatusAssignedToTaskEvent) {
        taskStatusId = event.taskStatusId
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun executorAssignedToTaskEventApply(event: ExecutorAssignedToTaskEvent) {
        executorIds.add(event.executorId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun executorRetractedFromTaskEventApply(event: ExecutorRetractedFromTaskEvent) {
        executorIds.remove(event.executorId)
        updatedAt = event.createdAt
    }
}