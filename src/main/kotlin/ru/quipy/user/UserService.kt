package ru.quipy.user

import com.google.common.eventbus.EventBus
import javassist.NotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import ru.quipy.user.dto.UserModel
import ru.quipy.user.dto.UserRegister
import java.lang.Exception

interface UserService {
    fun createOne(data: UserRegister): UserModel
    fun getOne(username: String): UserModel
}



@Service
class UserServiceImpl(
        private val userRepository: UserRepository,
): UserService {

    override fun createOne(data: UserRegister): UserModel {
        var foundUser: UserModel? = null
        try {
            foundUser = getOne(data.username)
        } catch (e: Exception) {
            // skip if exists
        }
        if (foundUser != null) throw IllegalStateException("user already exists")

        val userEntity = userRepository.save(data.toEntity())
        EventBus().post(UserCreatedEvent(userEntity.toModel()))
        return userEntity.toModel()
    }

    override fun getOne(username: String): UserModel {
        val userEntity: UserEntity =
                userRepository.findByUsername(username) ?: throw NotFoundException("user not found")
        return userEntity.toModel()
    }

    fun UserRegister.toEntity(): UserEntity =
            UserEntity(
                    username = this.username,
                    name = this.name,
                    password = BCryptPasswordEncoder().encode(this.password)
            )

    fun UserEntity.toModel(): UserModel = kotlin.runCatching {
        UserModel(
                id = this.id,
                username = this.username!!,
                name = this.name!!,
                password = this.password!!
        )
    }.getOrElse { exception -> throw IllegalStateException("Some fields are missing", exception) }
}