package service.usermservice.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.core.env.Environment
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import service.usermservice.client.OrderServiceClient
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
    private val orderServiceClient: OrderServiceClient,
    private val circuitBreakerFactory: CircuitBreakerFactory<*, *>,
//    private val restTemplate: RestTemplate
) : UserService {
    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    @Transactional
    override fun createUser(userDto: UserDto): UserDto {
        userDto.userId = UUID.randomUUID().toString()

        val userEntity = userDto.toUserEntity()
        userEntity.encryptPwd(bCryptPasswordEncoder.encode(userDto.pwd))
        userRepository.save(userEntity)

        return userDto
    }

    @Transactional(readOnly = true)
    override fun getUserByUserId(userId: String): UserDto {
        val userEntity = userRepository.findByUserId(userId) ?: throw UsernameNotFoundException("User not found")
        val userDto = UserDto.fromUserEntity(userEntity)

//        val orders = ArrayList<ResponseOrder>()

//        val orderUrl = String.format(env.getProperty("order_service.url")!!, userId)
//        val responseOrderList = restTemplate.exchange(
//            orderUrl,
//            HttpMethod.GET,
//            null,
//            object : ParameterizedTypeReference<List<ResponseOrder>>() {}
//        )
//        val orderList = responseOrderList.body

//        val orderList =
//            kotlin.runCatching { orderServiceClient.getOrders(userId) }
//                .onFailure { logger.error(it.message) }
//                .getOrNull()

        /* Error Decoder */
//        val orderList = orderServiceClient.getOrders(userId)
        logger.info("Before call orders microservice.")
        val circuitBreaker = circuitBreakerFactory.create("circuitbreaker")
        val orderList = circuitBreaker.run(
            { orderServiceClient.getOrders(userId) },
            { _: Throwable ->
                println("Failed to get orderList....")
                emptyList<ResponseOrder>()
            }
        )
        logger.info("After called orders microservice")

        userDto.orders = orderList

        return userDto
    }

    @Transactional(readOnly = true)
    override fun getUserByAll(): Iterable<UserEntity> {
        return userRepository.findAll()
    }

    @Transactional(readOnly = true)
    override fun getUserDetailByEmail(email: String): UserDto {
        val userEntity = userRepository.findByEmail(email) ?: throw UsernameNotFoundException("User not found")

        return UserDto.fromUserEntity(userEntity)
    }

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String?): UserDetails {
        val userEntity = username?.let { userRepository.findByEmail(it) } ?: throw UsernameNotFoundException(username)

        return User(userEntity.email, userEntity.encryptedPwd, true, true, true, true, ArrayList())
    }
}
