package ru.quipy.user

import org.springframework.web.bind.annotation.*
import ru.quipy.user.dto.UserModel
import ru.quipy.user.dto.UserRegister

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun register(@RequestBody request: UserRegister) = userService.createOne(request)

    @GetMapping("/{username}")
    fun getUser(@PathVariable username: String): UserModel =
            userService.getOne(username)
}