//package ru.quipy.api
//
//import ru.quipy.core.annotations.DomainEvent
//import java.util.*
//import ru.quipy.domain.Event
//
//
//
//const val USER_REGISTERED_EVENT = "UserRegisteredEvent"
//@DomainEvent(name = USER_REGISTERED_EVENT)
//class UserRegisteredEvent(
//    val userId: UUID,
//    val nickname: String,
//    val userName: String,
//    createdAt: Long = System.currentTimeMillis()
//) : Event<UserAggregate>(
//    name = USER_REGISTERED_EVENT,
//    createdAt = createdAt
//)
//
//const val USER_ADDED_TO_PROJECT_EVENT = "UserAddedToProjectEvent"
//@DomainEvent(name = USER_ADDED_TO_PROJECT_EVENT)
//class UserAddedToProjectEvent(
//    val userId: UUID,
//    val projectId: UUID,
//    createdAt: Long = System.currentTimeMillis()
//) : Event<UserAggregate>(
//    name = USER_ADDED_TO_PROJECT_EVENT,
//    createdAt = createdAt
//)
//
//const val USER_PROJECT_CREATED_EVENT = "UserProjectCreatedEvent"
//@DomainEvent(name = USER_PROJECT_CREATED_EVENT)
//class UserProjectCreatedEvent(
//    val userId: UUID,
//    val projectName: String,
//    createdAt: Long = System.currentTimeMillis()
//) : Event<UserAggregate>(
//    name = USER_PROJECT_CREATED_EVENT,
//    createdAt = createdAt
//)