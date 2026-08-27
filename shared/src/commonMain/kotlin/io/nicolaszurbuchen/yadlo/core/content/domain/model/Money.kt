package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * [amount] is held as authored — 4.5 means CHF 4.50. Minor units would mean inventing a scale the
 * content never used.
 */
data class Money(
    val amount: Double,
    val currency: String,
)
