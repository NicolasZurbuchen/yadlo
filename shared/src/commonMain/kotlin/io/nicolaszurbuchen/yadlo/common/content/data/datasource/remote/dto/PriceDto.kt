package io.nicolaszurbuchen.yadlo.common.content.data.datasource.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PriceDto(
    val free: Boolean,
    val tiers: List<TierDto> = emptyList(),
    val deposit: DepositDto? = null,
    val provenance: String,
) {
    @Serializable
    data class TierDto(
        val label: String? = null,
        val amount: Double,
        val currency: String,
        val per: String? = null,
    )

    @Serializable
    data class DepositDto(
        val amount: Double,
        val currency: String,
        val note: String? = null,
    )
}
