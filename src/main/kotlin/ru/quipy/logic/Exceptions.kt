package ru.quipy.logic

class UniqueConstraintViolation(message: String) : IllegalStateException(message)

class NoSuchEntity(message: String) : IllegalStateException(message)
