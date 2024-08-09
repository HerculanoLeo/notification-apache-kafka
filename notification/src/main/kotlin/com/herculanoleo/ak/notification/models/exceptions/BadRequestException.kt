package com.herculanoleo.ak.notification.models.exceptions

data class BadRequestException(override val message: String) : RuntimeException(message)
