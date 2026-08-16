package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * One way in or out — by bus, by night bus, by car, on two feet or two wheels, or across the water.
 *
 * **[facts] and [body] are alternatives, not layers.** A mode that is really a list of conditions —
 * which lines, which stop, how far the walk — is published as facts, because a sentence holding
 * three of them is a sentence somebody has to read to the end of before knowing whether it was
 * theirs. The modes that genuinely are prose keep [body] and publish no facts. Neither is a heading
 * with nothing under it, which is why the validator rejects that combination.
 *
 * [departures] is empty on every mode but the night bus. It is the one mode with times rather than
 * either, and they are grouped **by night rather than one row per bus**: seven departures read as
 * four lines instead of filling the screen, and the last Saturday bus — the one with no onward
 * connection to Lausanne — carries a note instead of being buried in a list.
 */
data class TransportMode(
    val id: String,
    val name: String,
    val body: String?,
    val facts: List<Fact>,
    val links: List<InfoLink>,
    val departures: List<Departure>,
) {
    /**
     * One stated condition of arriving this way.
     *
     * [caveat] is a boolean for the same reason a payment method's `accepted` is: a fact is either
     * something the site offers or a warning about it. *Places limitées* is not a facility, it is
     * what will go wrong, and the screen marks the two differently.
     */
    data class Fact(
        val id: String,
        val text: String,
        val caveat: Boolean,
    )

    data class Departure(
        val id: String,
        /** The night it leaves *after* — "Vendredi" covers a 02:00 bus on the Saturday morning. */
        val night: String,
        val times: List<Time>,
    ) {
        /**
         * [time] is carried as the published string rather than parsed into an Instant. A timetable
         * is a wall of wall-clock times with no date attached, and giving each one a day so it could
         * be reparsed back into the same string would be inventing the fact the night grouping
         * already states.
         */
        data class Time(
            val time: String,
            val note: String?,
        )
    }
}
