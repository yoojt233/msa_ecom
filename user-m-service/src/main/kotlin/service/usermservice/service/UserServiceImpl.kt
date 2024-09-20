package service.usermservice.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import service.usermservice.dto.UserDto
import service.usermservice.entity.UserEntity
import service.usermservice.repository.UserRepository
import service.usermservice.vo.ResponseOrder
import java.util.UUID

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    fun createUser(userDto: UserDto): UserDto {
        userDto.userId = UUID.randomUUID().toString()

        val userEntity = userDto.toUserEntity()
        userEntity.encryptPwd(passwordEncoder.encode(userDto.pwd))
        userRepository.save(userEntity)

        return userDto
    }

    fun getUserByUserId(userId: String): UserDto {
        val userEntity = userRepository.findByUserId(userId) ?: throw UsernameNotFoundException("User not found")
        val userDto = UserDto.fromUserEntity(userEntity)

        val orders = ArrayList<ResponseOrder>()
        userDto.orders = orders

        return userDto
    }

    fun getUserByAll(): Iterable<UserEntity> {
        return userRepository.findAll()
    }
}
