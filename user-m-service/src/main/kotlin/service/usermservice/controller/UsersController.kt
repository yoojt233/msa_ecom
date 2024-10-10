package service.usermservice.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import service.usermservice.service.UserServiceImpl
import service.usermservice.vo.Greeting
import service.usermservice.vo.RequestUser
import service.usermservice.vo.ResponseUser

@RestController
@RequestMapping("/")
class UsersController @Autowired constructor(
    private val env: Environment,
    private val greeting: Greeting,
    private val userServiceImpl: UserServiceImpl
) {

    @GetMapping("/health-check")
    fun status(request: HttpServletRequest): String {
        return String.format("It's Working in User Service on Port %s", request.serverPort)
    }

    @GetMapping("/welcome")
    fun welcome(): String {
        return greeting.getMessage() ?: "Hello world"
//        return env.getProperty("greeting") ?: "Hello world"
    }

    @PostMapping("/users")
    fun createUser(@RequestBody @Valid user: RequestUser): ResponseEntity<ResponseUser> {
        val temp = userServiceImpl.createUser(user.toUserDto())
        val response = temp?.run { ResponseUser(temp) }

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<ResponseUser>> {
        val userList = userServiceImpl.getUserByAll()
        val result = userList.map { ResponseUser.fromUserEntity(it) }.toList()

        return ResponseEntity.status(HttpStatus.OK).body(result)
    }

    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable("userId") userId: String): ResponseEntity<ResponseUser> {
        val userDto = userServiceImpl.getUserByUserId(userId)
        val result = userDto.toResponseUser()

        return ResponseEntity.status(HttpStatus.OK).body(result)
    }
}
