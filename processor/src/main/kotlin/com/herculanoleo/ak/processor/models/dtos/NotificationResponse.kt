package com.herculanoleo.ak.processor.models.dtos

import java.time.OffsetDateTime
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val subject: String,
    val content: String,
    val recipient: List<String>,
    val createdAt: OffsetDateTime,
    var sentAt: OffsetDateTime?,
    var type: String,
    var attempt: Int,
    var status: String,
)
