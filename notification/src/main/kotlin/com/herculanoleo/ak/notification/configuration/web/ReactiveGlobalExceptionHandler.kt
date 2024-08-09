package com.herculanoleo.ak.notification.configuration.web

import com.herculanoleo.ak.notification.models.dtos.ErrorResponse
import com.herculanoleo.ak.notification.models.dtos.FailedValidationResponse
import com.herculanoleo.ak.notification.models.dtos.MessageFields
import com.herculanoleo.ak.notification.models.dtos.SimpleErrorResponse
import com.herculanoleo.ak.notification.models.exceptions.BadRequestException
import com.herculanoleo.ak.notification.models.exceptions.NotFoundException
import lombok.extern.slf4j.Slf4j
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Slf4j
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
class ReactiveGlobalExceptionHandler : ResponseEntityExceptionHandler() {

    override fun handleWebExchangeBindException(
        ex: WebExchangeBindException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        exchange: ServerWebExchange
    ): Mono<ResponseEntity<Any>> {

        val fields = ex.bindingResult.fieldErrors.map {
            MessageFields(
                it.field,
                it.defaultMessage ?: "invalid data"
            )
        }

        return Mono.just(
            FailedValidationResponse(
                HttpStatus.BAD_REQUEST.value(),
                "The request contains invalid data",
                fields
            )
        ).flatMap { build(HttpStatus.BAD_REQUEST, it) }
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException) = buildSimple(HttpStatus.BAD_REQUEST, ex.message)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException) = buildSimple(HttpStatus.NOT_FOUND, ex.message)

    private fun buildSimple(httpStatus: HttpStatus, message: String?) = build(
        httpStatus,
        SimpleErrorResponse(httpStatus.value(), message)
    )

    private fun build(httpStatus: HttpStatus, body: ErrorResponse?): Mono<ResponseEntity<Any>> = Mono.just(
        ResponseEntity.status(httpStatus).body(body)
    )

}
