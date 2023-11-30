package ru.quipy.states.projectManagment

import ru.quipy.aggregates.projectManagment.ProjectAggregate
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.entities.projectManagment.Project
import ru.quipy.entities.projectManagment.Status
import ru.quipy.entities.projectManagment.Task
import ru.quipy.events.projectManagment.project.AssigneeAddedEvent
import ru.quipy.events.projectManagment.project.ParticipantAddedEvent
import ru.quipy.events.projectManagment.project.ProjectCreatedEvent
import ru.quipy.events.projectManagment.project.StatusAddedEvent
import ru.quipy.events.projectManagment.project.StatusRemovedEvent
import ru.quipy.events.projectManagment.project.TaskChangedEvent
import ru.quipy.events.projectManagment.project.TaskCreatedEvent
import java.util.UUID

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    lateinit var project: Project

    override fun getId() = project.id

    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        project = Project(
            id = event.projectId,
            name = event.projectName,
            creatorId = event.creatorId,
            statuses = mutableMapOf<UUID, Status>().apply {
                val defaultStatusId = DEFAULT_STATUS.id
                this[defaultStatusId] = Status(defaultStatusId, DEFAULT_STATUS.name, DEFAULT_STATUS.colorCode)
            },
            participants = mutableListOf(event.creatorId),
            tasks = mutableMapOf()
        )
    }

    @StateTransitionFunc
    fun statusCreatedApply(event: StatusAddedEvent) {
        val statuses = project.statuses
        statuses[event.statusId] ?: run {
            statuses[event.statusId] = Status(
                id = event.statusId,
                name = event.statusName,
                colorCode = event.colorCode,
            )
            project.updatedAt = event.createdAt
        }
    }

    @StateTransitionFunc
    fun statusRemovedApply(event: StatusRemovedEvent) {
        val statuses = project.statuses
        statuses[event.statusId]?.apply {
            if (this.name == DEFAULT_STATUS.name) {
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
                it.name == DEFAULT_STATUS.name
            }
            tasks[event.taskId] = Task(
                event.taskId,
                event.taskName,
                event.projectId,
                defaultStatus.id,
                mutableListOf()
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
        val DEFAULT_STATUS : Status = Status(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "DefaultStatus",
            "#000000"
        )
    }
}