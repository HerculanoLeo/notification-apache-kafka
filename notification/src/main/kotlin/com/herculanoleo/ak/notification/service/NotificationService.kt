package com.herculanoleo.ak.notification.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.herculanoleo.ak.notification.models.constants.KAFKA_NOTIFICATION_RESULT_TOPIC
import com.herculanoleo.ak.notification.models.dtos.NotificationRegisterRequest
import com.herculanoleo.ak.notification.models.dtos.NotificationRequest
import com.herculanoleo.ak.notification.models.dtos.NotificationUpdateRequest
import com.herculanoleo.ak.notification.models.dtos.SendEmailRequest
import com.herculanoleo.ak.notification.models.exceptions.NotFoundException
import com.herculanoleo.ak.notification.models.mappers.NotificationMapper
import com.herculanoleo.ak.notification.persistence.entities.Notification
import com.herculanoleo.ak.notification.persistence.repositories.NotificationRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
class NotificationService(
    private val repository: NotificationRepository,
    private val mapper: NotificationMapper,
    private val emailSenderService: EmailSenderService,
    private val objectMapper: ObjectMapper
) {
    fun findAll(requestEntity: NotificationRequest) = Flux.just(requestEntity)
        .flatMap { repository.findAll() }
        .map { mapper.map(it) }

    fun findById(id: UUID) = Mono.just(id)
        .flatMap { repository.findById(it) }
        .switchIfEmpty(Mono.error(NotFoundException("Not found")))
        .map { mapper.map(it) }

    fun register(requestEntity: NotificationRegisterRequest) = Mono.just(requestEntity)
        .map {
            Notification(
                it.subject!!,
                it.content!!,
                it.recipient!!.joinToString(";"),
                it.type!!,
                "R"
            )
        }
        .flatMap { repository.save(it) }
        .flatMap {
            if (it.type == "E") {
                return@flatMap emailSenderService.sendEmail(
                    SendEmailRequest(
                        it.id!!,
                        it.subject,
                        it.content,
                        it.recipient.split(";")
                    )
                ).then(Mono.just(it))
            }
            return@flatMap Mono.just(it)
        }
        .map { mapper.map(it) }

    fun update(id: UUID, requestEntity: NotificationUpdateRequest) = Mono.just(id)
        .flatMap { repository.findById(it) }
        .flatMap {
            it.status = requestEntity.status
            it.sentAt = requestEntity.sentAt
            repository.save(it)
        }
        .map { mapper.map(it) }

    @KafkaListener(topics = [KAFKA_NOTIFICATION_RESULT_TOPIC])
    fun notificationResultListener(record: ConsumerRecord<String, String>) {
        Mono.just(record)
            .flatMap {
                val id = UUID.fromString(record.key())
                val value = record.value()
                val requestEntity = objectMapper.readValue(value, NotificationUpdateRequest::class.java)
                this.update(id, requestEntity)
            }
            .subscribe()
    }

}
