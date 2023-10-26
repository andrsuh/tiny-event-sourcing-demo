//package ru.quipy.logic
//
//import ru.quipy.api.*
//import ru.quipy.core.annotations.StateTransitionFunc
//import ru.quipy.domain.AggregateState
//import java.util.*
//
//class UserAggregateState : AggregateState<UUID, UserAggregate> {
//    private lateinit var userId: UUID;
//    var createdAt: Long = System.currentTimeMillis()
//    var updatedAt: Long = System.currentTimeMillis()
//
//    lateinit var nickname: String
//    lateinit var name: String
// //   lateinit var projectName: String
//    lateinit var password: String
//
//    override fun getId() = userId
//
//    @StateTransitionFunc
//    fun registerUserApply(event: UserRegisteredEvent) {
//        userId = event.userId
//        nickname = event.nickname
//        name = event.name
//        updatedAt = createdAt
//  //      projectName = "0"
//    }
//
//    @StateTransitionFunc
//    fun ProjectAggregateState.userAddedToProjectApply(event: UserAddedToProjectEvent) {
//        if (participantsID.contains(event.userId)) {
//            throw IllegalArgumentException("User already a participant: ${event.userId}")
//        }
//        participantsID.add(event.userId)
//        updatedAt = System.currentTimeMillis()
//    }
//
//    @StateTransitionFunc
//    fun ProjectAggregateState.userProjectCreatedApply(event: UserProjectCreatedEvent) {
//        if (this.getId() != null) {
//            throw IllegalStateException("This project already exists.")
//        }
//
//        this.name = event.projectName
//        this.createdAt = System.currentTimeMillis()
//        this.updatedAt = System.currentTimeMillis()
//        participantsID.add(event.userId) // Добавляем создателя в участники
//    }
//}