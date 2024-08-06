package com.herculanoleo.ak.notification.models.dtos

import java.util.*

data class SendEmailRequest(
    val id: UUID,
    val subject: String,
    val content: String,
    val recipient: List<String>,
)
