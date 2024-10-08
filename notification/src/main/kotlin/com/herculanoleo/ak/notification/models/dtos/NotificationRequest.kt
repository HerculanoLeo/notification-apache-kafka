package com.herculanoleo.ak.notification.models.dtos

data class NotificationRequest(
    val email: String?,
    val status: String?,
    val attemptLtEq: Int?,
    val inStatus: List<String>?,
)
