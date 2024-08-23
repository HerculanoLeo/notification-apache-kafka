package com.herculanoleo.ak.processor.openfeign.clients

import com.herculanoleo.ak.processor.models.dtos.NotificationResponse
import com.herculanoleo.ak.processor.models.dtos.NotificationRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.cloud.openfeign.SpringQueryMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import java.util.*

@FeignClient(
    name = "notification-client",
    url = "\${app.notification.url}/notification",
)
interface NotificationClient {

    @GetMapping
    fun findAll(@SpringQueryMap  requestEntity: NotificationRequest): List<NotificationResponse>

    @PutMapping("{id}/resend")
    fun resend(@PathVariable("id") id: UUID)

}
