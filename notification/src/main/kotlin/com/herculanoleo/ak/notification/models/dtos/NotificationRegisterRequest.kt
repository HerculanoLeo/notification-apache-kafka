package com.herculanoleo.ak.notification.models.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class NotificationRegisterRequest(
    @field:NotBlank
    val subject: String?,
    @field:NotBlank
    val content: String?,
    @field:NotNull @field:NotEmpty
    val recipient: List<String>?,
    @field:NotBlank
    val type: String?,
)
