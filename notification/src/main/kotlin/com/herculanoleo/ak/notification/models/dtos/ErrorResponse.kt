package com.herculanoleo.ak.notification.models.dtos

import java.time.OffsetDateTime

interface ErrorResponse {
    val statusCode: Int
    val message: String?
    val datetime: OffsetDateTime
}
