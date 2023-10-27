package ru.quipy.logic.project

import ru.quipy.api.project.ProjectCreatedEvent
import ru.quipy.api.project.StatusCreatedEvent
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = id,
        title = title,
        creatorId = creatorId,
    )
}

fun ProjectAggregateState.createStatus(name: String): StatusCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Status already exists: $name")
    }
    return StatusCreatedEvent(projectId = this.getId(), statusId = UUID.randomUUID(), statusName = name)
}

//fun ProjectAggregateState.assignTagToTask(statusId: UUID, taskId: UUID): TagAssignedToTaskEvent {
//    if (!projectTags.containsKey(statusId)) {
//        throw IllegalArgumentException("Tag doesn't exists: $statusId")
//    }
//
//    if (!tasks.containsKey(taskId)) {
//        throw IllegalArgumentException("Task doesn't exists: $taskId")
//    }
//
//    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = statusId, taskId = taskId)
//}

//fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
//    return TaskCreatedEvent(
//        projectId = this.getId(),
//        taskId = UUID.randomUUID(),
//        taskName = name
//    )
//}