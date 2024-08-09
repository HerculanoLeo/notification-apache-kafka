package com.herculanoleo.ak.notification.models.exceptions

data class NotFoundException(override val message: String?): RuntimeException(message)
