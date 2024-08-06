package com.herculanoleo.ak.notification.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.herculanoleo.ak.notification.models.constants.KAFKA_SEND_EMAIL_TOPIC
import com.herculanoleo.ak.notification.models.dtos.SendEmailRequest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class EmailSenderService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {

    fun sendEmail(requestEntity: SendEmailRequest) = Mono.just(requestEntity)
        .flatMap {
            Mono.fromFuture(
                kafkaTemplate.send(
                    KAFKA_SEND_EMAIL_TOPIC,
                    it.id.toString(),
                    objectMapper.writeValueAsString(it)
                )
            )
        }

}
