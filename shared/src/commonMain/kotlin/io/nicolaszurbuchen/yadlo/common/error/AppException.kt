package io.nicolaszurbuchen.yadlo.common.error

class AppException(
    val error: AppError,
) : Exception("App error: $error")
