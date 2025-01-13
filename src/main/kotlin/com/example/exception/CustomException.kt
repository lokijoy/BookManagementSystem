package com.example.exception

class DbAccessException(message: String) : RuntimeException(message)
class NotFoundException(message: String) : RuntimeException(message)
class BadRequestException(message: String) : RuntimeException(message)
