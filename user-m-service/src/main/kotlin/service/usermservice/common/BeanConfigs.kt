package service.usermservice.common

import feign.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class BeanConfigs {

    @Bean
    fun getBCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun getFeignLogger(): Logger.Level = Logger.Level.FULL

//    @Bean
//    @LoadBalanced
//    fun getRestTemplate(): RestTemplate = RestTemplate()
}