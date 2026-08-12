package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * What an Activity costs. One shape for every activity, free or not.
 *
 * The content used to carry three mutually exclusive shapes here — a bare `free` flag, a flat
 * amount, and a tier list — so every screen showing a price had to work out which one it was
 * holding before it could read a number. They are now a single shape, and the content validator
 * holds the invariants rather than this class defending against them.
 *
 * [free] and [tiers] are two views of one fact: [tiers] is empty exactly when [free] is true. Both
 * exist because "gratuit" is a thing the UI writes, not a thing it infers from an empty list.
 */
data class Price(
    val free: Boolean,
    val tiers: List<Tier>,
    val deposit: Deposit?,
    val provenance: Provenance,
) {
    /**
     * [label] is null when there is one price for everyone, and [per] is null when that price is
     * per person — the two defaults that cover every activity but the Silent Party and the two
     * escape games.
     */
    data class Tier(
        val label: String?,
        val amount: Money,
        val per: String?,
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
