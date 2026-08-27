package io.nicolaszurbuchen.yadlo.core.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MoneyDto(
    val amount: Double,
    val currency: String,
)
