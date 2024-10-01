package service.usermservice.vo

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class RequestLogin(
    @Email
    @NotNull(message = "Email cannot be null.")
    @Size(min = 2, message = "Email not be less than two characters.")
    val email: String,

    @NotNull(message = "Password cannot be null.")
    @Size(
        min = 8,
        max = 16,
        message = "Password must be equal or grater than 8 characters and less than 16 characters."
    )
    val password: String
)
