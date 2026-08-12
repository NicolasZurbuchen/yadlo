package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * An amount and its currency.
 *
 * [amount] is an Int because every price in the content is a whole franc or a half — and the halves
 * are written as 4.5 by the content author, not as 450 centimes. Storing minor units would mean the
 * mapper inventing a scale the source never used, so this holds the value as authored and formatting
 * decides how to render it.
 */
data class Money(
    val amount: Double,
    val currency: String,
)
