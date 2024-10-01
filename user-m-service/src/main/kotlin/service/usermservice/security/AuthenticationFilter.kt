package service.usermservice.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import service.usermservice.vo.RequestLogin
import java.io.IOException

class AuthenticationFilter : UsernamePasswordAuthenticationFilter() {

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        try {
            val creds = ObjectMapper().readValue(request.inputStream, RequestLogin::class.java)

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

    // TODO: 사용자 ID와 PWD를 통해 로그인 성공 시에 대한 처리 작업
    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        chain: FilterChain?,
        authResult: Authentication?
    ) {
        super.successfulAuthentication(request, response, chain, authResult)
    }
}