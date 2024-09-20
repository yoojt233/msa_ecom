package service.usermservice.vo

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import service.usermservice.dto.UserDto

data class RequestUser(
    @field:NotBlank(message = "Email cannot be blank.")
    @Email
    @Size(min = 2, message = "Email not be less than two characters.")
    val email: String,

    @field:NotBlank(message = "Password cannot be blank.")
    @Size(
        min = 8,
        max = 16,
        message = "Password must be equal or grater than 8 characters and less than 16 characters."
    )
    var pwd: String,

    @field:NotBlank(message = "Name cannot be blank.")
    @Size(min = 2, message = "Name not be less than two characters.")
    var name: String
) {
    fun toUserDto(): UserDto {
        return UserDto(this)
    }
}