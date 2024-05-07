package ru.quipy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaskManager

fun main(args: Array<String>) {
	runApplication<TaskManager>(*args)
}
