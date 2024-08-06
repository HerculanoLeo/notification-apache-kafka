package com.herculanoleo.ak.notification.models.mappers

import com.herculanoleo.ak.notification.models.dtos.NotificationResponse
import com.herculanoleo.ak.notification.persistence.entities.Notification
import org.springframework.stereotype.Component

@Component
class NotificationMapper {

    fun map(entityToMap: Notification): NotificationResponse {
        return NotificationResponse(
            entityToMap.id!!,
            entityToMap.subject,
            entityToMap.content,
            entityToMap.recipient.split(";"),
            entityToMap.createdAt,
            entityToMap.sentAt,
            entityToMap.type,
            entityToMap.status
        )
    }

    fun map(entitiesToMap: List<Notification>?): List<NotificationResponse> {
        return entitiesToMap?.map { map(it) } ?: emptyList()
    }
}
