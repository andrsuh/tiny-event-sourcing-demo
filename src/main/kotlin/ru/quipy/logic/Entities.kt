package ru.quipy.logic

import java.util.*

//data class TaskEntity(
//    val id: UUID = UUID.randomUUID(),
//    val title: String,
//    //val status: StatusEntity,
//    val tagsAssigned: MutableSet<UUID>
//)

data class StatusEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)