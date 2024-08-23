package com.herculanoleo.ak.notification.models.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.OffsetDateTime

data class NotificationUpdateRequest(
    val status: String,
    val sentAt: OffsetDateTime?,
    @field:JsonIgnore
    var attempt: Int = 1
)
