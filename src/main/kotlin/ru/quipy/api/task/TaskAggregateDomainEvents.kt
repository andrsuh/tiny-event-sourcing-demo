//package ru.quipy.api
//
//import ru.quipy.core.annotations.DomainEvent
//import ru.quipy.domain.Event
//import java.util.*
//
//const val TASK_CREATED_EVENT = "TASK_CREATED_EVENT"
//const val STATUS_ASSIGNED_TO_TASK_EVENT = "STATUS_ASSIGNED_TO_TASK_EVENT"
//const val TASK_DATA_CHANGED_EVENT = "TASK_DATA_CHANGED_EVENT"
//
//// API
//@DomainEvent(name = TASK_CREATED_EVENT)
//class TaskCreatedEvent(
//    val taskId: UUID,
//    val projectId: UUID,
//    val title: String,
//    createdAt: Long = System.currentTimeMillis(),
//) : Event<TaskAggregate>(
//    name = TASK_CREATED_EVENT,
//    createdAt = createdAt,
//)
//
//@DomainEvent(name = STATUS_ASSIGNED_TO_TASK_EVENT)
//class StatusAssignedToTaskEvent(
//    val projectId: UUID,
//    val taskId: UUID,
//    val statusId: UUID,
//    createdAt: Long = System.currentTimeMillis(),
//) : Event<TaskAggregate>(
//    name = TAG_ASSIGNED_TO_TASK_EVENT,
//    createdAt = createdAt
//)
//
//@DomainEvent(name = TASK_DATA_CHANGED_EVENT)
//class TaskDataChangedEvent(
//    val taskId: UUID,
//    val projectId: UUID,
//    val title: String,
//    createdAt: Long = System.currentTimeMillis(),
//) : Event<TaskAggregate>(
//    name = TASK_CREATED_EVENT,
//    createdAt = createdAt,
//)
//
