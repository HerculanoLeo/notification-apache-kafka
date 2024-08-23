package com.herculanoleo.ak.notification.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.herculanoleo.ak.notification.extentions.inSQL
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
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.OffsetDateTime
import java.util.*

@Service
class NotificationService(
    private val databaseClient: DatabaseClient,
    private val repository: NotificationRepository,
    private val mapper: NotificationMapper,
    private val emailSenderService: EmailSenderService,
    private val objectMapper: ObjectMapper,
) {

    fun findAll(requestEntity: NotificationRequest) = Flux.just(requestEntity)
        .flatMap {
            var queryStr = """
                    SELECT n.id, n.subject, n.content, n.recipient, n.created_at, n.sent_at, n.type, n.attempt, n.status
                    FROM tb_notification n
                """.trimIndent()

            val source = mutableMapOf<String, Any>()

            var whereStr = " WHERE 1=1 "

            if (requestEntity.status?.isNotBlank() == true) {
                whereStr += " AND UPPER(n.status) = UPPER(:status) "
                source["status"] = requestEntity.status
            }

            if (requestEntity.attemptLtEq != null) {
                whereStr += " AND n.attempt <= :attemptLtEq "
                source["attemptLtEq"] = requestEntity.attemptLtEq
            }

            if (requestEntity.inStatus != null) {
                whereStr += " AND UPPER(n.status) IN (${requestEntity.inStatus.inSQL()}) "
            }

            queryStr += whereStr

            queryStr += " ORDER BY n.created_at ASC, n.attempt DESC "

            this.databaseClient.sql(queryStr)
                .bindValues(source)
                .map { row ->
                    Notification(
                        row["id"] as UUID,
                        row["subject"] as String,
                        row["content"] as String,
                        row["recipient"] as String,
                        row["created_at"] as OffsetDateTime,
                        row["sent_at"] as? OffsetDateTime,
                        row["type"] as String,
                        row["attempt"] as Int,
                        row["status"] as String,
                    )
                }.all()
        }
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
            return@flatMap sendNotification(it).then(Mono.just(it))
        }
        .map { mapper.map(it) }

    fun update(id: UUID, requestEntity: NotificationUpdateRequest) = Mono.just(id)
        .flatMap { repository.findById(it) }
        .flatMap {
            it.status = requestEntity.status
            it.sentAt = requestEntity.sentAt
            it.attempt += requestEntity.attempt
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

    fun resend(id: UUID) = repository.findById(id)
        .switchIfEmpty { Mono.error(NotFoundException("Not found")) }
        .flatMap {
            this.sendNotification(it)
        }

    private fun sendNotification(it: Notification) = Mono.just(it)
        .flatMap {
            if (it.type == "E") {
                return@flatMap emailSenderService.sendEmail(
                    SendEmailRequest(
                        it.id!!,
                        it.subject,
                        it.content,
                        it.recipient.split(";")
                    )
                ).then()
            }
            return@flatMap Mono.empty()
        }

}
