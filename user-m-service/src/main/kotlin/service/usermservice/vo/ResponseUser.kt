package service.usermservice.vo

import com.fasterxml.jackson.annotation.JsonInclude
import service.usermservice.dto.UserDto
import service.usermservice.entity.UserEntity

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseUser(
    val email: String,
    val name: String,
    val userId: String,
    val orders: List<ResponseOrder>
) {
    constructor(userDto: UserDto) : this(userDto.email, userDto.name, userDto.userId, userDto.orders)

    companion object {
        fun fromUserEntity(userEntity: UserEntity): ResponseUser {
            return ResponseUser(userEntity.email, userEntity.name, userEntity.userId, ArrayList())
        }
    }
}
