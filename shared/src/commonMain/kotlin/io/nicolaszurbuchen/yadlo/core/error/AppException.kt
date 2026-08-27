package io.nicolaszurbuchen.yadlo.core.error

class AppException(
    val error: AppError,
) : Exception("App error: $error")
