package ru.quipy.dto

import java.util.*

class TaskDto {
    lateinit var taskTitle: String
    lateinit var description: String
    lateinit var projectId: UUID
    lateinit var statusId: UUID
}