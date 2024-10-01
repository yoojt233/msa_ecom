package service.usermservice.service

import org.springframework.security.core.userdetails.UserDetailsService
import service.usermservice.dto.UserDto
import service.usermservice.entity.UserEntity

interface UserService : UserDetailsService {
    fun createUser(userDto: UserDto): UserDto
    fun getUserByUserId(userId: String): UserDto
    fun getUserByAll(): Iterable<UserEntity>
}