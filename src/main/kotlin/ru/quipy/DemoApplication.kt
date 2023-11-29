package ru.quipy

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import ru.quipy.user.UserRepository

@SpringBootApplication
@EnableMongoRepositories
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}
