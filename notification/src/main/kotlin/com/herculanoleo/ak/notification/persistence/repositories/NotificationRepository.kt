package com.herculanoleo.ak.notification.persistence.repositories

import com.herculanoleo.ak.notification.persistence.entities.Notification
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface NotificationRepository : ReactiveCrudRepository<Notification, UUID>
