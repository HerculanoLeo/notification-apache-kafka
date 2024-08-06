package com.herculanoleo.ak.emailsender.models.dtos

import java.time.OffsetDateTime

data class NotificationUpdateRequest(
    val status: String,
    val sentAt: OffsetDateTime? = OffsetDateTime.now(),
)
