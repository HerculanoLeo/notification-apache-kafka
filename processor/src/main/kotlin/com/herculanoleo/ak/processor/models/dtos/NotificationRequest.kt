package com.herculanoleo.ak.processor.models.dtos

data class NotificationRequest(
    val email: String?,
    val status: String?,
    val attemptLtEq: Int?,
    val inStatus: List<String>?,
)
