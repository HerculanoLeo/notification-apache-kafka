package com.herculanoleo.ak.notification.persistence.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.*

@Table(name = "tb_notification")
data class Notification(
    @Id
    @Column("id")
    val id: UUID?,
    val subject: String,
    val content: String,
    val recipient: String,
    @Column("created_at")
    val createdAt: OffsetDateTime,
    @Column("sent_at")
    var sentAt: OffsetDateTime?,
    var type: String,
    var attempt: Int,
    var status: String,
) {
    constructor(
        subject: String,
        content: String,
        recipient: String,
        type: String,
        status: String,
    ) : this(null, subject, content, recipient, OffsetDateTime.now(), null, type, 0, status)
}
