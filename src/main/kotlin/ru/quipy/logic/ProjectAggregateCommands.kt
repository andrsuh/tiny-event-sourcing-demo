package ru.quipy.logic

import com.fasterxml.jackson.databind.BeanDescription
import org.yaml.snakeyaml.events.Event.ID
import ru.quipy.api.*
import java.util.*


// Commands : takes something -> returns event
// Here the commands are represented by extension functions, but also can be the class member functions

fun ProjectAggregateState.createProject(title: String, creatorId: String, description: String): ProjectCreatedEvent {
    return ProjectCreatedEvent(
        projectId = UUID.randomUUID(),
        title = title,
        creatorId = creatorId,
        description = description
    )
}

fun ProjectAggregateState.createStatus(projectId: UUID, name: String, color: String): StatusCreatedEvent {
    if (statuses.values.any { it.statusName == name }) {
        throw IllegalArgumentException("Tag already exists: $name")
    }
    return StatusCreatedEvent(
        projectId = projectId, 
        statusId = UUID.randomUUID(), 
        statusName = name,
        statusColor = color
    )
}

fun ProjectAggregateState.deleteStatus(projectId: UUID, statusId: UUID): StatusDeletedEvent {
    if (!statuses.values.any { it.statusId == statusId }) {
        throw IllegalArgumentException("Tag does not exist")
    }
    return StatusDeletedEvent(
        projectId = this.getId(),
        statusId = statusId
    )
}