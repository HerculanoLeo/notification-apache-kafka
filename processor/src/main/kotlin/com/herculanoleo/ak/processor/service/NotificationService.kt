package com.herculanoleo.ak.processor.service

import com.herculanoleo.ak.processor.models.dtos.NotificationRequest
import com.herculanoleo.ak.processor.openfeign.clients.NotificationClient
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationService(
    private val notificationClient: NotificationClient
) {
    fun findAll(requestEntity: NotificationRequest) = notificationClient.findAll(requestEntity)

    fun resend(id: UUID) = notificationClient.resend(id)
}
