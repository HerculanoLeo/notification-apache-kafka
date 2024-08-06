package com.herculanoleo.ak.emailsender.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.herculanoleo.ak.emailsender.configuration.web.EmailAttributes
import com.herculanoleo.ak.emailsender.models.constants.KAFKA_NOTIFICATION_RESULT_TOPIC
import com.herculanoleo.ak.emailsender.models.constants.KAFKA_SEND_EMAIL_TOPIC
import com.herculanoleo.ak.emailsender.models.dtos.NotificationUpdateRequest
import com.herculanoleo.ak.emailsender.models.dtos.SendEmailRequest
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class EmailSenderService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val mailSender: JavaMailSender,
    private val emailAttributes: EmailAttributes
) {

    @KafkaListener(topics = [KAFKA_SEND_EMAIL_TOPIC])
    protected fun sendEmailListener(record: ConsumerRecord<String, String>) {
        val id = record.key()
        val value = record.value()
        Mono.just(value)
            .map { objectMapper.readValue(it, SendEmailRequest::class.java) }
            .flatMap { this.sendEmail(it) }
            .doOnError {
                it.printStackTrace()
                kafkaTemplate.send(
                    KAFKA_NOTIFICATION_RESULT_TOPIC,
                    id,
                    objectMapper.writeValueAsString(NotificationUpdateRequest("E", null))
                )
            }
            .then(Mono.defer {
                Mono.fromFuture(
                    kafkaTemplate.send(
                        KAFKA_NOTIFICATION_RESULT_TOPIC,
                        id,
                        objectMapper.writeValueAsString(NotificationUpdateRequest("S"))
                    )
                )
            })
            .subscribe()
    }

    fun sendEmail(requestEntity: SendEmailRequest) = Mono.just(requestEntity)
        .map {
            if (emailAttributes.enabled) {
                val message = mailSender.createMimeMessage()
                val helper = MimeMessageHelper(message, true, "UTF-8")

                helper.setFrom(emailAttributes.from, emailAttributes.personalName)

                it.recipient.forEach { to -> helper.addTo(to) }

                helper.setSubject(it.subject)
                helper.setText(it.content, true)

                mailSender.send(message)
            }
        }.then()

}
