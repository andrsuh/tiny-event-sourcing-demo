package ru.quipy.projection.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.projection.dto.UserDto
import ru.quipy.projection.service.UserService
import java.util.*

@RestController
@RequestMapping("/user")
class UserViewController(
    val userService: UserService
) {

    @GetMapping("/{id}")
    fun getUserByNickname(@PathVariable id: UUID): UserDto? {
        return userService.findUserById(id);
    }
}