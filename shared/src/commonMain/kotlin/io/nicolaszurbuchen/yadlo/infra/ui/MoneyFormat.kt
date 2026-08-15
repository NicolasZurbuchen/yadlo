package io.nicolaszurbuchen.yadlo.infra.ui

import kotlin.math.roundToLong

/**
 * `CHF 5`, `CHF 4.50` — the currency as authored, then the amount.
 *
 * Written by hand rather than with a platform formatter because there isn't one in common code, and
 * because the two locales that matter here disagree about the separator while the festival's own
 * price lists do not: everything the association publishes is written `CHF 4.50`.
 *
 * Whole amounts drop the decimals. Most prices at Yadlo are round francs, and `CHF 5.00` in a list
 * of them reads as a till receipt rather than as a programme.
 *
 * Takes the two fields rather than a `Money` so it stays usable from the presentation layer, which
 * is not allowed to import the domain.
 */
fun formatMoney(
    amount: Double,
    currency: String,
): String {
    // Via a whole number of rappen: the amounts arrive as authored decimals, and comparing or
    // splitting a Double on its fractional part is how CHF 4.50 becomes CHF 4.49.
    val rappen = (amount * RAPPEN_PER_FRANC).roundToLong()
    val francs = rappen / RAPPEN_PER_FRANC
    val remainder = (rappen % RAPPEN_PER_FRANC).toInt()

    return if (remainder == 0) {
        "$currency $francs"
    } else {
        "$currency $francs.${remainder.toString().padStart(2, '0')}"
    }
}

private const val RAPPEN_PER_FRANC = 100L
