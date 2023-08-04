package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    lateinit var name: String
    lateinit var creatorId: UUID
    val members = mutableSetOf<UUID>()
    val taskStatuses = mutableMapOf<UUID, TaskStatus>()

    override fun getId() = projectId

    @StateTransitionFunc
    fun apply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        name = event.projectName
        creatorId = event.creatorId
        val defaultTaskStatus = TaskStatus(
            id = event.defaultStatusId,
            name = "CREATED",
            color = Color("#777777"),
            isDefault = true,
            ordinalNumber = 1,
        )
        taskStatuses[defaultTaskStatus.id] = defaultTaskStatus
    }

    @StateTransitionFunc
    fun apply(event: ProjectMemberAddedEvent) {
        members.add(event.memberId)
    }

    @StateTransitionFunc
    fun apply(event: TaskStatusCreatedEvent) {
        taskStatuses[event.statusId] = TaskStatus(
            id = event.statusId,
            name = event.statusName,
            color = event.color,
            ordinalNumber = taskStatuses.values.maxOf { it.ordinalNumber } + 1,
        )
    }

    @StateTransitionFunc
    fun apply(event: TaskStatusDeletedEvent) {
        taskStatuses.remove(event.statusId)
    }

    @StateTransitionFunc
    fun apply(event: TaskStatusesOrderSetEvent) {
        event.statusesIds.forEachIndexed { index, statusId ->
            taskStatuses.getValue(statusId).ordinalNumber = index + 1
        }
    }

    fun defaultStatus(): TaskStatus {
        return taskStatuses.values.first { it.isDefault }
    }
}

data class TaskStatus(
    val id: UUID,
    val name: String,
    val color: Color,
    val isDefault: Boolean = false,
    var ordinalNumber: Int,
)

data class Color(val hexCode: String)
