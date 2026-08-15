package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * One way in or out — on foot, by bike, by bus, by night bus, by car, or across the water.
 *
 * [departures] is empty on every mode but the night bus. It is the one mode with times rather than
 * prose, and they are grouped **by night rather than one row per bus**: seven departures read as
 * four lines instead of filling the screen, and the last Saturday bus — the one with no onward
 * connection to Lausanne — carries a note instead of being buried in a list.
 */
data class TransportMode(
    val id: String,
    val name: String,
    val body: String?,
    val links: List<InfoLink>,
    val departures: List<Departure>,
) {
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
