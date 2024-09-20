package service.usermservice.vo

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class Greeting {

    @Value("\${greeting.message}")
    private var message: String? = null

    fun getMessage(): String? {
        return this.message
    }
}