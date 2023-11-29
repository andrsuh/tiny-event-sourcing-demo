package ru.quipy

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.server.ResponseStatusException
import ru.quipy.user.UserEntity
import ru.quipy.user.UserServiceImpl
import ru.quipy.user.dto.UserLogin
import ru.quipy.user.dto.UserModel
import ru.quipy.user.dto.UserRegister

@SpringBootTest
class UserTests {
	companion object {
		private val username = "admin"
		private val realName = "Dimas"
		private val password = "12345678"

		private val registerDTO = UserRegister(
				username, password, realName
		)
		private val loginDTO = UserLogin(
				username, password
		)
		private val wrongUsernameLoginDTO = UserLogin(
				"Diman", loginDTO.password
		)
		private val wrongPasswordLoginDTO = UserLogin(
				username, BCryptPasswordEncoder().encode("12345677")
		)
		private val fullyWrongCredentialsLoginDTO = UserLogin(
				wrongUsernameLoginDTO.username, wrongPasswordLoginDTO.password
		)
	}

	@Autowired
	lateinit var userEsService: UserServiceImpl

	@Autowired
	lateinit var mongoTemplate: MongoTemplate

	@BeforeEach
	fun cleanDatabase() {
		try {
			mongoTemplate.remove(Query.query(Criteria.where("userId").`is`(userEsService.getOne(username).userId)),
							UserEntity::class.java)
		} catch (e: ResponseStatusException) {
			if (e.status != HttpStatus.NOT_FOUND)
			{
				throw e
			}
		}
	}

	@Test
	fun registerNewUser() {
		var userModel: UserModel? = null

		Assertions.assertDoesNotThrow( {
			userModel = userEsService.createOne(registerDTO)
		}, "can't create new user")

		Assertions.assertAll(
				Executable { Assertions.assertTrue(BCryptPasswordEncoder().matches(password, userModel!!.password),
						"encoded passwords doesn't match") },
				Executable { Assertions.assertEquals(userModel!!.realName, realName, "names doesn't match") },
				Executable { Assertions.assertEquals(userModel!!.username, username,
						"usernames doesn't match") })
	}

	@Test
	fun registerExistingUser() {
		Assertions.assertDoesNotThrow( {
			userEsService.createOne(registerDTO)
		}, "can't create new user")

		Assertions.assertEquals(
				Assertions.assertThrows(ResponseStatusException::class.java) {
					userEsService.createOne(registerDTO) }.status,
				HttpStatus.CONFLICT, "create user with existing username"
		)
	}

	@Test
	fun loginUser() {
		Assertions.assertDoesNotThrow( {
			userEsService.createOne(registerDTO)
		}, "can't create new user")
		Assertions.assertDoesNotThrow( {
			userEsService.logIn(loginDTO)
		}, "can't login with correct credentials")
	}

	@Test
	fun loginWithWrongCredentials() {
		Assertions.assertDoesNotThrow {
			userEsService.createOne(registerDTO)
		}

		Assertions.assertAll(
				Executable { Assertions.assertEquals(
						Assertions.assertThrows(ResponseStatusException::class.java) {
							userEsService.logIn(wrongUsernameLoginDTO)
						}.status, HttpStatus.NOT_FOUND, "login with wrong username successfully"
				) },
				Executable { Assertions.assertEquals(
						Assertions.assertThrows(ResponseStatusException::class.java) {
							userEsService.logIn(wrongPasswordLoginDTO)
						}.status, HttpStatus.CONFLICT, "login with wrong password successfully"
				) },
				Executable { Assertions.assertEquals(
						Assertions.assertThrows(ResponseStatusException::class.java) {
							userEsService.logIn(fullyWrongCredentialsLoginDTO)
						}.status, HttpStatus.NOT_FOUND, "login with all credentials wrong successfully"
				) }
		)
	}

	@Test
	fun getExistingUser() {
		var userModel: UserModel? = null
		var sameUserModel: UserModel? = null

		Assertions.assertDoesNotThrow( {
			userModel = userEsService.createOne(registerDTO)
		}, "can't create new user")

		Assertions.assertDoesNotThrow( {
			sameUserModel = userEsService.getOne(userModel!!.username)
		}, "can't find created user")

		Assertions.assertEquals(userModel, sameUserModel, "created user doesn't exist")
	}

	@Test
	fun getNonExistingUser() {
		Assertions.assertDoesNotThrow( {
			userEsService.createOne(registerDTO)
		}, "can't create new user")

		Assertions.assertEquals(
				Assertions.assertThrows(ResponseStatusException::class.java) {
					userEsService.getOne(realName)
				}.status, HttpStatus.NOT_FOUND, "found non existing user"
		)
	}
}
