package service.usermservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager
import service.usermservice.service.UserService

@Configuration
@EnableWebSecurity
class WebSecurity @Autowired constructor(
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val userService: UserService,
    private val env: Environment
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder)

        val authenticationManager = authenticationManagerBuilder.build()

        http
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(PathRequest.toH2Console()).permitAll()
                    .requestMatchers("/**")
                    .access(WebExpressionAuthorizationManager("hasIpAddress('127.0.0.1')"))
                    .anyRequest().authenticated()
            }
            .authenticationManager(authenticationManager)
            .addFilter(getAuthenticationFilter(authenticationManager))
            .headers { header -> header.frameOptions { it.disable() } }

        return http.build()
    }

    fun getAuthenticationFilter(authenticationManager: AuthenticationManager): AuthenticationFilter {
        val filter = AuthenticationFilter(authenticationManager, userService, env)

        return filter
    }
}
