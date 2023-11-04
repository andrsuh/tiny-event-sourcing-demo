package ru.quipy.commands.projectManagment.project

import ru.quipy.events.projectManagment.project.TaskRenamedEvent
import ru.quipy.states.projectManagment.ProjectAggregateState
import java.util.UUID

fun ProjectAggregateState.renameTask(taskId: UUID, newTaskName: String) : TaskRenamedEvent {
    if (!this.tasks.containsKey(taskId)) {
        throw IllegalArgumentException("Project doesn't have task with id $taskId")
    }
    return TaskRenamedEvent(
        this.getId(),
        taskId,
        newTaskName,
    )
}