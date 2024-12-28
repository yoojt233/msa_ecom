package service.ordermservice.mq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.stream.Environment
import org.springframework.stereotype.Service
import service.ordermservice.dto.OrderDto
import service.ordermservice.repository.OrderRepository
import java.nio.charset.StandardCharsets

@Service
class RabbitProducer(private val orderRepository: OrderRepository) {
    val objectMapper = ObjectMapper()
    val env = Environment.builder().build()

    fun sendQty(stream: String, orderDto: OrderDto): OrderDto {
        declare(stream)

        val producer = env.producerBuilder()
            .stream(stream)
            .build()

        runCatching { objectMapper.writeValueAsString(orderDto) }
            .onSuccess {
                producer
                    .send(producer.messageBuilder().addData(it.toByteArray(StandardCharsets.UTF_8)).build()) { cfs ->
                        if (!cfs.isConfirmed) println("Fail to send message. Message: ${cfs.message}, Code: ${cfs.code}")
                    }
            }
            .onFailure { e -> e.printStackTrace() }

        return orderDto
    }

    fun declare(stream: String) {
        if (!env.streamExists(stream)) env.streamCreator().stream(stream).create()
    }
}
