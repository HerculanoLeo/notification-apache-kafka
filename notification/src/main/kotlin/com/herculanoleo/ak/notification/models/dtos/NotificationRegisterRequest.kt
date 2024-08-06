package com.herculanoleo.ak.notification.models.dtos

data class NotificationRegisterRequest(
    val subject: String,
    val content: String,
    val recipient: Array<String>,
    val type: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationRegisterRequest

        if (subject != other.subject) return false
        if (content != other.content) return false
        if (!recipient.contentEquals(other.recipient)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = subject.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + recipient.contentHashCode()
        return result
    }
}
