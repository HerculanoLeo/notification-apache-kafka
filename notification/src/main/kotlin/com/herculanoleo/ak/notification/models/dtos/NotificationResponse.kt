package com.herculanoleo.ak.notification.models.dtos

import java.time.OffsetDateTime
import java.util.*

data class NotificationResponse(
    val id: UUID = UUID.randomUUID(),
    val subject: String,
    val content: String,
    val recipient: List<String>,
    val createdAt: OffsetDateTime,
    var sentAt: OffsetDateTime?,
    var type: String,
    var status: String,
)
