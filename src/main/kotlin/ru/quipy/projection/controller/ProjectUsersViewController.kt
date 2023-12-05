package ru.quipy.projection.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.quipy.projection.dto.UserDto
import ru.quipy.projection.service.ProjectUsersService
import java.util.*

@RestController
@RequestMapping("/project")
class ProjectUsersViewController(
    val projectUsersService: ProjectUsersService
) {

    @GetMapping("/{projectId}/users")
    fun getUsersByProjectId(@PathVariable projectId: UUID): List<UserDto> {
        return projectUsersService.findUsersByProjectId(projectId)
    }
}