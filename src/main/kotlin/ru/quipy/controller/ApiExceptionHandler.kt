package ru.quipy.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import ru.quipy.exception.NotFoundException

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handle(e: NotFoundException) = ResponseEntity<String>(e.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(IllegalStateException::class)
    fun handle(e: IllegalStateException) = ResponseEntity<String>(e.message, HttpStatus.CONFLICT)

    @ExceptionHandler(Throwable::class)
    fun handle(e: Throwable) = ResponseEntity<String>("Internal error", HttpStatus.INTERNAL_SERVER_ERROR)

}