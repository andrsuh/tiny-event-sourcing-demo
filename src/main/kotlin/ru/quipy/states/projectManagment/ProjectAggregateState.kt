package ru.quipy.states.projectManagment

import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.commands.projectManagment.project.create
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.entities.projectManagment.Project
import ru.quipy.entities.projectManagment.Status
import ru.quipy.entities.projectManagment.Task
import ru.quipy.events.projectManagment.project.*
import java.awt.Color
import java.util.UUID

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    lateinit var project: Project

    override fun getId() = project.id

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        project = Project(
            id = event.projectId,
            name = event.name,
            creatorId = event.creatorId,
            statuses = mutableMapOf<UUID, Status>().apply {
                val defaultStatusId = UUID.randomUUID()
                this[defaultStatusId] = Status(defaultStatusId, DEFAULT_STATUS_NAME, Color.BLACK)
            }
        )
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusAddedEvent) {
        val statuses = project.statuses
        statuses[event.statusId]?.apply {
            statuses[event.statusId] = this.copy(
                name = event.statusName,
                color = event.color,
            )
            project.updatedAt = event.createdAt
        }
    }

    @StateTransitionFunc
    fun statusRemovedApply(event: StatusRemovedEvent) {
        val statuses = project.statuses
        statuses[event.statusId]?.apply {
            if (this.name == DEFAULT_STATUS_NAME) {
                return
            }
            statuses.remove(event.statusId)
            project.updatedAt = event.createdAt
        }
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        val tasks = project.tasks
        tasks[event.taskId] ?: run {
            val defaultStatus = project.statuses.values.first {
                it.name == DEFAULT_STATUS_NAME
            }
            tasks[event.taskId] = Task(
                event.taskId,
                event.taskName,
                event.projectId,
                defaultStatus.id,
            )
            project.updatedAt = event.createdAt
        }
    }

    @StateTransitionFunc
    fun taskChangedApply(event: TaskChangedEvent) {
        val tasks = project.tasks
        tasks[event.taskId]?.apply {
            tasks[event.taskId] = this.copy(
                name = event.newTaskName ?: this.name,
                statusId = event.newStatusId ?: this.statusId
            )
            project.updatedAt = event.createdAt
        }
    }

    @StateTransitionFunc
    fun participantAddedApply(event: ParticipantAddedEvent) {
        val participants = project.participants
        participants.firstOrNull { it == event.userId } ?: run {
            participants.add(event.userId)
            project.updatedAt = event.createdAt
        }
    }

    @StateTransitionFunc
    fun assigneeAddedApply(event: AssigneeAddedEvent) {
        val tasks = project.tasks

        tasks[event.taskId]?.assigneeIds?.apply {
            if (event.userId in this) {
                return
            }
            add(event.userId)
            project.updatedAt = event.createdAt
        }

    }


    companion object {
        private const val DEFAULT_STATUS_NAME = "DefaultStatus"
    }
}