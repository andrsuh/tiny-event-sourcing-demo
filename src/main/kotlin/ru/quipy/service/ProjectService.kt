package ru.quipy.service

import org.springframework.stereotype.Service
import ru.quipy.projections.Project
import ru.quipy.projections.UserAccount
import ru.quipy.projections.ProjectCacheRepository
import java.util.*

@Service
class ProjectService (
    val projectCacheRepository: ProjectCacheRepository
) {

    fun getAllProjects(): MutableList<Project> {
        return projectCacheRepository.findAll()
    }
}