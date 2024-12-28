package service.usermservice.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import service.usermservice.service.UserService
import service.usermservice.vo.Greeting
import service.usermservice.vo.RequestUser
import service.usermservice.vo.ResponseUser

@RestController
@RequestMapping("/")
class UsersController @Autowired constructor(
    private val env: Environment,
    private val greeting: Greeting,
    private val userService: UserService
) {

    @GetMapping("/health-check")
    fun status(request: HttpServletRequest): String {
        return String.format(
            "It's Working in User Service"
                    + ", port(local.server.port)=" + env.getProperty("local.server.port")
                    + ", port(server.port)=" + env.getProperty("server.port")
                    + ", with token secret=" + env.getProperty("token.secret")
                    + ", with token time=" + env.getProperty("token.expiration_time")
        )
    }

    @GetMapping("/welcome")
    fun welcome(): String {
        return greeting.getMessage() ?: "Hello world"
//        return env.getProperty("greeting") ?: "Hello world"
    }

    @PostMapping("/users")
    fun createUser(@RequestBody @Valid user: RequestUser): ResponseEntity<ResponseUser> {
        val temp = userService.createUser(user.toUserDto())
        val response = temp?.run { ResponseUser(temp) }

        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<ResponseUser>> {
        val userList = userService.getUserByAll()
        val result = userList.map { ResponseUser.fromUserEntity(it) }.toList()

        return ResponseEntity.status(HttpStatus.OK).body(result)
    }

    @GetMapping("/users/{userId}")
    fun getUser(@PathVariable("userId") userId: String): ResponseEntity<ResponseUser> {
        val userDto = userService.getUserByUserId(userId)
        val result = userDto.toResponseUser()

        return ResponseEntity.status(HttpStatus.OK).body(result)
    }
}
