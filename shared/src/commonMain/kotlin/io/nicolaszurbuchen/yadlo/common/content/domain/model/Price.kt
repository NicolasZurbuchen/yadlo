package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * One shape for every Activity, free or not. [tiers] is empty exactly when [free] is true, and the
 * content validator holds that invariant so this class does not have to defend against it.
 */
data class Price(
    val free: Boolean,
    val tiers: List<Tier>,
    val deposit: Deposit?,
    val provenance: Provenance,
) {
    /** [label] is null when one price covers everyone, [per] when that price is per person. */
    data class Tier(
        val label: String?,
        val amount: Money,
        val per: String?,
    )

    /**
     * Never summed into the price: the Silent Party is CHF 25 with a CHF 50 headset deposit, and
     * CHF 75 is wrong in the direction that stops someone coming.
     */
    data class Deposit(
        val amount: Money,
        val note: String?,
    )
}
