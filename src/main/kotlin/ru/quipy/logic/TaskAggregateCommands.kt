package ru.quipy.logic

import ru.quipy.api.TaskStatusChangedEvent
import java.util.*




/*
TODO: add missing fields to TaskAggregateState

fun TaskAggregateState.assignStatusToTask(tagId: UUID, taskId: UUID): TaskStatusChangedEvent {
    if (!projectTags.containsKey(tagId)) {
        throw IllegalArgumentException("Tag doesn't exists: $tagId")
    }

    if (!tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Task doesn't exists: $taskId")
    }

    return TaskStatusChangedEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
}*/
