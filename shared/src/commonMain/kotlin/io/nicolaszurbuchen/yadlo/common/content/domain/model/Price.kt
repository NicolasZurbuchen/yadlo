package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * What an Activity costs.
 *
 * [tiers] rather than a single amount because the Silent Party charges adults and under-16s
 * differently, and an empty list is how the content says "free" — the association's posters carry a
 * price when there is one and nothing when there is not, so silence is a statement rather than an
 * omission.
 */
data class Price(
    val tiers: List<Tier>,
    val deposit: Deposit?,
    val provenance: Provenance,
) {
    val isFree: Boolean
        get() = tiers.isEmpty()

    data class Tier(
        val label: String?,
        val amount: Money,
    )

    /**
     * A refundable deposit, which is not part of what the activity costs and must never be summed
     * into it: the Silent Party is CHF 25 with a CHF 50 headset deposit, and showing CHF 75 would be
     * wrong in the direction that stops someone coming.
     */
    data class Deposit(
        val amount: Money,
        val note: String?,
    )
}
