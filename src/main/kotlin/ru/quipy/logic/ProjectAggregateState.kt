package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: UUID
    var tasks: List<UUID> = emptyList()
    var projectTags = mutableMapOf<UUID, TagEntity>()
    var projectMembers = mutableMapOf<UUID, ProjectMemberEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
        projectMembers = mutableMapOf()
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName, event.color)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun userAssignedToProjectEventApply(event: UserAssignedToProjectEvent) {
        if (projectMembers.contains(event.userId)) throw Exception("This user has already been added to this project");
        projectMembers[event.userId] = ProjectMemberEntity(event.userId, event.username, event.nickname)
        updatedAt = System.currentTimeMillis()
    }
}


data class TagEntity(
        val id: UUID = UUID.randomUUID(),
        var name: String,
        var color: String
)

data class ProjectMemberEntity(
        val id: UUID = UUID.randomUUID(),
        val name: String,
        val nickname: String
)

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */

@StateTransitionFunc
fun ProjectAggregateState.tagChangeNameApply(event: TagChangeNameEvent) {
    projectTags[event.tagId]?.name = event.tagName
    updatedAt = createdAt
}

@StateTransitionFunc
fun ProjectAggregateState.tagChangeColorApply(event: TagChangeColorEvent) {
    projectTags[event.tagId]?.color = event.color
    updatedAt = createdAt
}
