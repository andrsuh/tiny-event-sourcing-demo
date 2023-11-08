package ru.quipy.logic.project

import ru.quipy.api.project.*
import java.lang.IllegalStateException
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

fun ProjectAggregateState.addTask(name: String): TaskCreatedEvent {
    return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
}

fun ProjectAggregateState.createTag(name: String): TagCreatedEvent {
    if (projectTags.values.any { it.name == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name)
}

fun ProjectAggregateState.assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}

fun ProjectAggregateState.addMember(projectId: UUID, userId: UUID): MemberAddedEvent {
    if (!memberIds.contains(userId)) {
        memberIds.add(userId)
    }
    return MemberAddedEvent(
        projectId = projectId,
        userId = userId
    )
}

fun ProjectAggregateState.changeProjectName(projectId: UUID, newProjectName: String): ProjectNameChangedEvent {
    return ProjectNameChangedEvent(
        projectId = projectId,
        projectName = newProjectName
    )
}

fun ProjectAggregateState.createStatus(projectId: UUID, statusName: String, color: String): StatusCreatedEvent {

    val statusAlreadyExists = projectStatuses.stream()
        .anyMatch { st -> st.name == statusName }
    if (statusAlreadyExists) {
        throw IllegalStateException("Status already exists: $statusName")
    }

    return StatusCreatedEvent(
        projectId = projectId,
        statusName = statusName,
        color = color
    )
}

//ToDo уточнить, необходима ли такая команда и transition-function, т.к. мы не храним Задачи в агрегате Project, а храним только список taskIds.
// Соответственно здесь не можем поменять статус задачи
fun ProjectAggregateState.setTaskStatus(taskId: UUID, statusId: UUID): TaskStatusChangedEvent {
    return TaskStatusChangedEvent(
        taskId = taskId,
        statusId = statusId
    )
}


fun ProjectAggregateState.deleteStatus(projectId: UUID, statusId: UUID): StatusDeletedEvent {

    val statusEntity = projectStatuses.stream()
        .filter({ st -> st.id.equals(statusId) })
        .findFirst()

    if (statusEntity.isPresent) {
        val isStatusAssignedToAnyTask = tasks.values.stream()
            .anyMatch { task -> task.statusId.equals(statusId) }
        if (isStatusAssignedToAnyTask) {
            throw IllegalStateException("Status ${statusEntity.get().name} can't be deleted because it's assigned to at least 1 task")
        }
        return StatusDeletedEvent(
            projectId = projectId,
            statusId = statusId
        )
    } else {
        throw IllegalStateException("Status: ${statusEntity.get().name} does not exists in project")
    }
}


