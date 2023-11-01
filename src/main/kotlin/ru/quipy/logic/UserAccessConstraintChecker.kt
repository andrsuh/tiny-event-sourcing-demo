package ru.quipy.logic

import ru.quipy.logic.project.ProjectAggregateState
import java.util.*

class UserAccessConstraintChecker {
    companion object {
        fun canAccess(project: ProjectAggregateState, userId: UUID) {
            if (project.creatorId != userId && !project.memberIds.contains(userId)) {
                throw IllegalArgumentException("User with id=$userId cannot access project with id=${project.getId()}")
            }
        }
    }
}