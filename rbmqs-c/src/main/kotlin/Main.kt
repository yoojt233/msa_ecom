package service

import com.rabbitmq.stream.Environment
import service.rabbit.RabbitConsumer

fun main() {
    val env = Environment.builder().build()
    val rabbitConsumer = RabbitConsumer(env)

    Runtime.getRuntime().addShutdownHook(Thread {
        rabbitConsumer.close()
    })

    rabbitConsumer.start()
}
