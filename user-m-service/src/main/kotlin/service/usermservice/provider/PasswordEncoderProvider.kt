package service.usermservice.provider

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderProvider {

    @Bean
    fun getBCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()
}
