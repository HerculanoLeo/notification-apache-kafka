package com.herculanoleo.ak.notification.models.dtos

import java.time.OffsetDateTime

data class NotificationUpdateRequest(
    val status: String,
    val sentAt: OffsetDateTime?,
)
