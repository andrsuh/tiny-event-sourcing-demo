//package ru.quipy.logic
//
//import ru.quipy.api.UserAddedToProjectEvent
//import ru.quipy.api.UserProjectCreatedEvent
//import java.util.*
//
//
//fun ProjectAggregateState.addUserToProject(userId: UUID, projectId: UUID): UserAddedToProjectEvent {
//    if (!this.getId().equals(projectId)) {
//        throw IllegalArgumentException("Mismatching project ID")
//    }
//
//    return UserAddedToProjectEvent(userId, projectId)
//}
//
//fun ProjectAggregateState.createUserProject(userId: UUID, projectName: String): UserProjectCreatedEvent {
//    return UserProjectCreatedEvent(userId, projectName)
//}
