package service.usermservice.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import service.usermservice.dto.UserDto
import service.usermservice.entity.UserEntity
import service.usermservice.repository.UserRepository
import service.usermservice.vo.ResponseOrder
import java.util.*

@Service
class UserServiceImpl @Autowired constructor(
    private val env: Environment,
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val restTemplate: RestTemplate
) : UserService {

    override fun createUser(userDto: UserDto): UserDto {
        userDto.userId = UUID.randomUUID().toString()

        val userEntity = userDto.toUserEntity()
        userEntity.encryptPwd(bCryptPasswordEncoder.encode(userDto.pwd))
        userRepository.save(userEntity)

        return userDto
    }

    override fun getUserByUserId(userId: String): UserDto {
        val userEntity = userRepository.findByUserId(userId) ?: throw UsernameNotFoundException("User not found")
        val userDto = UserDto.fromUserEntity(userEntity)

//        val orders = ArrayList<ResponseOrder>()
        println("Which I using RestTemplate Object is ${restTemplate.toString()} and ${restTemplate.hashCode()}")

        val orderUrl = String.format(env.getProperty("order_service.url")!!, userId)
        val responseOrderList = restTemplate.exchange(
            orderUrl,
            HttpMethod.GET,
            null,
            object : ParameterizedTypeReference<List<ResponseOrder>>() {}
        )

        userDto.orders = responseOrderList.body!!

        return userDto
    }

    override fun getUserByAll(): Iterable<UserEntity> {
        return userRepository.findAll()
    }

    override fun getUserDetailByEmail(email: String): UserDto {
        val userEntity = userRepository.findByEmail(email) ?: throw UsernameNotFoundException("User not found")

        return UserDto.fromUserEntity(userEntity)
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        val userEntity = username?.let { userRepository.findByEmail(it) } ?: throw UsernameNotFoundException(username)

        return User(userEntity.email, userEntity.encryptedPwd, true, true, true, true, ArrayList())
    }
}
