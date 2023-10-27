package ru.quipy.logic.project

import ru.quipy.api.project.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import ru.quipy.logic.StatusEntity
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: String // TODO: net v tz
    //var tasks = mutableMapOf<UUID, TaskEntity>()
    //var tasks = mutableMapOf<UUID, TaskAggregateState>()
    var projectTags = mutableMapOf<UUID, StatusEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: StatusCreatedEvent) {
        projectTags[event.statusId] = StatusEntity(event.statusId, event.statusName)
        updatedAt = createdAt
    }

//    @StateTransitionFunc
//    fun taskCreatedApply(event: TaskCreatedEvent) { // TODO: move into taskAggregate
//        //tasks[event.taskId] =
//        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, mutableSetOf())
//        updatedAt = createdAt
//    }

//    @StateTransitionFunc
//    fun tagAssignedApply(event: TagAssignedToTaskEvent) { // TODO: move into taskAggregate
//        tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
//            ?: throw IllegalArgumentException("No such task: ${event.taskId}")
//        updatedAt = createdAt
//    }
}