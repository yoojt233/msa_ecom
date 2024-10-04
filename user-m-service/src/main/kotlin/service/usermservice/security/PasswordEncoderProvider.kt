package service.usermservice.security

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderProvider {

    @Bean
    fun init(): BCryptPasswordEncoder = BCryptPasswordEncoder()
}
