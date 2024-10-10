package service.usermservice.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.env.Environment
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import service.usermservice.dto.UserDto
import service.usermservice.service.UserService
import service.usermservice.vo.RequestLogin
import java.io.IOException
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
    private val userService: UserService,
    private val env: Environment
) : UsernamePasswordAuthenticationFilter() {

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val jom = jacksonObjectMapper()

        try {
            val creds = jom.readValue(request.inputStream, RequestLogin::class.java)

            return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    creds.email,
                    creds.password,
                    ArrayList()
                )
            )
        } catch (e: IOException) {
            throw RuntimeException()
        }
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        val username = (authResult!!.principal as User).username
        val userDto: UserDto = userService.getUserDetailByEmail(username)


    }
}