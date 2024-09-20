package service.usermservice.dto

import service.usermservice.entity.UserEntity
import service.usermservice.vo.RequestUser
import service.usermservice.vo.ResponseOrder
import service.usermservice.vo.ResponseUser
import java.util.Date

data class UserDto(
    var email: String,
    var pwd: String,
    var name: String,
    var userId: String,
    var createdAt: Date,
    var encryptedPwd: String?,
    var orders: List<ResponseOrder>
) {
    constructor(requestUser: RequestUser) : this(
        requestUser.email,
        requestUser.pwd,
        requestUser.name,
        "",
        Date(),
        "",
        ArrayList<ResponseOrder>()
    )

    fun toUserEntity(): UserEntity {
        return UserEntity(this)
    }

    fun toResponseUser(): ResponseUser {
        return ResponseUser(this)
    }

    companion object {
        fun fromUserEntity(userEntity: UserEntity): UserDto {
            return UserDto(
                userEntity.email,
                "",
                userEntity.name,
                userEntity.userId,
                Date(),
                userEntity.encryptedPwd,
                ArrayList()
            )
        }
    }
}
