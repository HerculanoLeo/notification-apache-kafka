package com.herculanoleo.ak.emailsender.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.herculanoleo.ak.emailsender.configuration.web.EmailAttributes
import com.herculanoleo.ak.emailsender.models.constants.KAFKA_NOTIFICATION_RESULT_TOPIC
import com.herculanoleo.ak.emailsender.models.constants.KAFKA_SEND_EMAIL_TOPIC
import com.herculanoleo.ak.emailsender.models.dtos.NotificationUpdateRequest
import com.herculanoleo.ak.emailsender.models.dtos.SendEmailControl
import com.herculanoleo.ak.emailsender.models.dtos.SendEmailRequest
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

private const val EMAIL_CONTROL_STATUS_PROCESSING = "PROCESSING"
private const val EMAIL_CONTROL_STATUS_PROCESSED = "PROCESSED"
private const val EMAIL_CONTROL_STATUS_ERROR = "ERROR"

@Service
class EmailSenderService(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val mailSender: JavaMailSender,
    private val emailAttributes: EmailAttributes,
    private val redisTemplate: ReactiveRedisTemplate<String, Any>
) {
    private val log = LoggerFactory.getLogger(EmailSenderService::class.java)

    @KafkaListener(topics = [KAFKA_SEND_EMAIL_TOPIC])
    protected fun sendEmailListener(record: ConsumerRecord<String, String>) {
        val id = record.key()
        val value = record.value()
        Mono.just(value)
            .map {
                objectMapper.readValue(it, SendEmailRequest::class.java)
            }
            .flatMap {
                log.debug("Received email notification to send: {}", it.id)
                this.sendEmail(it).then(Mono.just(it))
            }
            .doOnError {
                it.printStackTrace()
                kafkaTemplate.send(
                    KAFKA_NOTIFICATION_RESULT_TOPIC,
                    id,
                    objectMapper.writeValueAsString(NotificationUpdateRequest("E", null))
                )
            }
            .flatMap { email ->
                Mono.fromFuture(
                    kafkaTemplate.send(
                        KAFKA_NOTIFICATION_RESULT_TOPIC,
                        id,
                        objectMapper.writeValueAsString(NotificationUpdateRequest("S"))
                    )
                ).flatMap { this.saveEmailControl(SendEmailControl(email.id, EMAIL_CONTROL_STATUS_PROCESSED)) }
            }
            .subscribe()
    }

    fun sendEmail(requestEntity: SendEmailRequest) = Mono.just(requestEntity)
        .filterWhen {
            log.debug("Verify if email should be send in control: {}", it.id)
            this.findEmailControlByIdRedis(it.id)
                .map { control ->
                    log.debug("A control have be found with status = {}: {}", control.status, it.id)
                    control.status == EMAIL_CONTROL_STATUS_ERROR
                }
                .switchIfEmpty(Mono.just(true))
        }
        .flatMap { email ->
            log.debug("Save email to control before sending to smtp: {}", email.id)
            this.saveEmailControl(
                SendEmailControl(
                    email.id,
                    EMAIL_CONTROL_STATUS_PROCESSING
                )
            ).map { email }
        }
        .map {
            log.debug("sending to smtp: {}", it.id)
            if (emailAttributes.enabled) {
                val message = mailSender.createMimeMessage()
                val helper = MimeMessageHelper(message, true, "UTF-8")

                helper.setFrom(emailAttributes.from, emailAttributes.personalName)

                it.recipient.forEach { to -> helper.addTo(to) }

                helper.setSubject(it.subject)
                helper.setText(it.content, true)

                mailSender.send(message)
            }
        }
        .onErrorResume {
            log.debug("an error occur when try to sending to smtp: {}", requestEntity.id)
            log.debug("saving error result in control: {}", requestEntity.id)
            this.saveEmailControl(SendEmailControl(requestEntity.id, EMAIL_CONTROL_STATUS_ERROR))
                .then(Mono.error(it))
        }
        .then()

    fun findEmailControlByIdRedis(id: UUID) = this.redisTemplate.opsForValue()
        .get("email-$id").map {
            objectMapper.convertValue(it, SendEmailControl::class.java)
        }

    fun saveEmailControl(emailControl: SendEmailControl) =
        this.redisTemplate.opsForValue().set("email-${emailControl.id}", emailControl)

}
