package service.usermservice.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import service.usermservice.entity.UserEntity

@Repository
interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByUserId(userId: String): UserEntity?
}