package ru.quipy.states.projectManagment

import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.entities.projectManagment.Status
import ru.quipy.entities.projectManagment.Task
import ru.quipy.events.projectManagment.project.AssigneeAddedEvent
import ru.quipy.events.projectManagment.project.ParticipantAddedEvent
import ru.quipy.events.projectManagment.project.ProjectCreatedEvent
import ru.quipy.events.projectManagment.project.StatusAddedEvent
import ru.quipy.events.projectManagment.project.StatusRemovedEvent
import ru.quipy.events.projectManagment.project.TaskCreatedEvent
import ru.quipy.events.projectManagment.project.TaskRenamedEvent
import ru.quipy.events.projectManagment.project.TaskStatusChangedEvent
import java.awt.Color
import java.util.UUID

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID

    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectName: String
    lateinit var creatorId: UUID
    val participants = mutableListOf<UUID>()
    val tasks = mutableMapOf<UUID, Task>()
    private val defaultStatusId = UUID.randomUUID()
    val statuses = mutableMapOf<UUID, Status>(
        Pair(
            defaultStatusId,
            Status(defaultStatusId, "DefaultStatus", Color.BLACK)
        )
    )

    override fun getId() = projectId

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectName = event.projectName
        creatorId = event.creatorId
        participants.add(creatorId)


        createdAt = event.createdAt
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusAddedEvent) {
        if (statuses.containsKey(event.statusId)) {
            throw IllegalArgumentException("Project already has status with id ${event.statusId}")
        }

        statuses[event.statusId] = Status(event.statusId, event.statusName, event.color)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun statusRemovedApply(event: StatusRemovedEvent) {
        if (!statuses.containsKey(event.statusId)) {
            throw IllegalArgumentException("Project doesn't have status with id ${event.statusId}")
        }
        tasks.remove(event.statusId)
        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        if (!tasks.containsKey(event.taskId)) {
            throw IllegalArgumentException("Project already has task with id ${event.taskId}")
        }
        tasks[event.taskId] = Task(
            event.taskId,
            event.taskName,
            event.projectId,
            defaultStatusId
        )

        updatedAt = event.createdAt
    }

    @StateTransitionFunc
    fun taskStatusChangedApply(event: TaskStatusChangedEvent) {
        if (!tasks.containsKey(event.taskId)) {
            throw IllegalArgumentException("Project doesn't have task with id ${event.taskId}")
        }
        if (!statuses.containsKey(event.statusId)) {
            throw IllegalArgumentException("Project doesn't have status with id ${event.statusId}")
        }

        tasks[event.taskId]!!.statusId = event.statusId

        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskRenamedChangedApply(event: TaskRenamedEvent) {
        if (!tasks.containsKey(event.taskId)) {
            throw IllegalArgumentException("Project doesn't have task with id ${event.taskId}")
        }

        tasks[event.taskId]!!.name = event.newTaskName

        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun participantAddedApply(event: ParticipantAddedEvent) {
        if (participants.contains(event.userId)) {
            throw IllegalArgumentException("Project already has task with id ${event.userId}")
        }

        participants.add(event.userId)

        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun assigneeAddedApply(event: AssigneeAddedEvent) {
        if (!participants.contains(event.userId)) {
            throw IllegalArgumentException("Project doesn't have participant with id ${event.userId}")
        }

        tasks[event.taskId]?.assigneeIds?.add(event.userId)
            ?: throw IllegalArgumentException("Project doesn't have task with id ${event.taskId}")

        updatedAt = createdAt
    }
}