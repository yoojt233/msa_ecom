package service.usermservice.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.util.matcher.IpAddressMatcher
import java.util.function.Supplier

@Configuration
@EnableWebSecurity
class WebSecurity(private val authenticationManagerBuilder: AuthenticationManagerBuilder) {
    val ALLOWED_IP_ADDRESS = ""
    val ALLOWED_IP_ADDRESS_MATCHER = IpAddressMatcher(ALLOWED_IP_ADDRESS)
    val authenticationManager = authenticationManagerBuilder.build()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers("/**").access(this::hasIpAddress).anyRequest().authenticated()
                authorize.requestMatchers(PathRequest.toH2Console()).permitAll()
            }
            .addFilter(getAuthenticationFilter(authenticationManager))
            .headers { header -> header.frameOptions { it.disable() } }

        return http.build()
    }

    fun hasIpAddress(
        authentication: Supplier<Authentication>,
        obj: RequestAuthorizationContext
    ): AuthorizationDecision {

        return AuthorizationDecision(
            authentication.get() !is AnonymousAuthenticationToken && ALLOWED_IP_ADDRESS_MATCHER.matches(obj.request)
        )
    }

    fun getAuthenticationFilter(authenticationManager: AuthenticationManager): AuthenticationFilter {
        val filter = AuthenticationFilter()

        filter.setAuthenticationManager(authenticationManager)

        return filter
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()
}
