package com.herculanoleo.ak.notification.controller

import com.herculanoleo.ak.notification.models.dtos.NotificationRegisterRequest
import com.herculanoleo.ak.notification.models.dtos.NotificationRequest
import com.herculanoleo.ak.notification.service.NotificationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/notification")
class NotificationController(private val service: NotificationService) {

    @GetMapping
    fun findAll(requestEntity: NotificationRequest) = ResponseEntity
        .ok(service.findAll(requestEntity))

    @GetMapping("{id}")
    fun findById(@PathVariable id: UUID) = ResponseEntity.ok(service.findById(id))

    @PostMapping
    fun register(@Valid @RequestBody requestEntity: NotificationRegisterRequest) = ResponseEntity
        .status(HttpStatus.CREATED)
        .body(service.register(requestEntity))

}
