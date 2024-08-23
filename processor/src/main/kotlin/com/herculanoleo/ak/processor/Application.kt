package com.herculanoleo.ak.processor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableFeignClients
@EnableScheduling
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
