package service.usermservice.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import service.usermservice.dto.UserDto

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue
    val id: Long,

    @Column(nullable = false, length = 50, unique = true)
    val email: String,

    @Column(nullable = false, length = 50)
    val name: String,

    @Column(nullable = false, length = 50, unique = true)
    val userId: String,

    @Column(nullable = false, unique = true)
    var encryptedPwd: String
) {
    constructor() : this(0L, "", "", "", "")
    constructor(userDto: UserDto) : this(0L, userDto.email, userDto.name, userDto.userId, "")

    fun encryptPwd(pwd: String) {
        this.encryptedPwd = pwd
    }
}
