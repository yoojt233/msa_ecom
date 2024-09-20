package com.example.ecomm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class EcommApplication

fun main(args: Array<String>) {
    runApplication<EcommApplication>(*args)
}
