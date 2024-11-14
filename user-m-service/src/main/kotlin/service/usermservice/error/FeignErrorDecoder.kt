package service.usermservice.error

import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException

@Component
class FeignErrorDecoder(private val env: Environment) : ErrorDecoder {

    override fun decode(methodKey: String?, response: Response?): Exception {
        if (response != null && methodKey != null) {
            when (response.status()) {
                404 -> {
                    if (methodKey.contains("getOrders")) return ResponseStatusException(
                        HttpStatus.valueOf(response.status()),
                        env.getProperty("order_service.exception.order_empty")
                    )
                }

                else -> Exception(response.reason())
            }
        }

        return Exception("Null is here.")
    }
}
