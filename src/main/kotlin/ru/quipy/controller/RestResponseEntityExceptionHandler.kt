package ru.quipy.controller

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import ru.quipy.logic.NoSuchEntity
import ru.quipy.logic.UniqueConstraintViolation
import java.lang.IllegalStateException

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(UniqueConstraintViolation::class)
    fun handleUniqueConstraintViolation(exception: Exception?, request: WebRequest?): ResponseEntity<Any> {
        return ResponseEntity(exception?.message, HttpHeaders(), HttpStatus.CONFLICT)
    }

    @ExceptionHandler(NoSuchEntity::class)
    fun handleNoSuchEntity(exception: Exception?, request: WebRequest?): ResponseEntity<Any> {
        return ResponseEntity(exception?.message, HttpHeaders(), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(exception: Exception?, request: WebRequest?): ResponseEntity<Any> {
        return ResponseEntity(exception?.message, HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY)
    }
}
