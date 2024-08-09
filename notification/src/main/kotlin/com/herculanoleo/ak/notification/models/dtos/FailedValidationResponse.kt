package com.herculanoleo.ak.notification.models.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.OffsetDateTime

data class FailedValidationResponse(
    override val statusCode: Int,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    override val message: String?,
    val fields: List<MessageFields>,
    override val datetime: OffsetDateTime = OffsetDateTime.now()
): ErrorResponse
