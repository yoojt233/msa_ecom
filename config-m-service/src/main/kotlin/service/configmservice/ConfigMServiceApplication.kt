package service.configmservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
class ConfigMServiceApplication

fun main(args: Array<String>) {
    runApplication<ConfigMServiceApplication>(*args)
}
