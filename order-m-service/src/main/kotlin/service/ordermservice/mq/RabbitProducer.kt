package service.ordermservice.mq

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.stream.impl.StreamEnvironmentBuilder
import org.springframework.stereotype.Service
import service.ordermservice.dto.OrderDto
import java.nio.charset.StandardCharsets

@Service
class RabbitProducer(env: org.springframework.core.env.Environment) {
    val objectMapper = ObjectMapper()
    val rabbit = StreamEnvironmentBuilder().port(5552).build()

    fun sendOrder(stream: String, orderDto: OrderDto): OrderDto {
        declare(stream)

        val producer = rabbit.producerBuilder()
            .stream(stream)
            .build()

        runCatching { objectMapper.writeValueAsString(orderDto) }
            .onSuccess {
                val message = producer.messageBuilder().properties().contentType("application/json")
                    .messageBuilder().addData(it.toByteArray(StandardCharsets.UTF_8)).build()

                producer.send(message) { cfs ->
                    if (!cfs.isConfirmed) println("Fail to send message. Message: ${cfs.message}, Code: ${cfs.code}")
                }
            }
            .onFailure { e -> e.printStackTrace() }

        return orderDto
    }

    fun declare(stream: String) {
        if (!rabbit.streamExists(stream)) rabbit.streamCreator().stream(stream).create()
    }
}
