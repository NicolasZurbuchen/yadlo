package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * Where the year is, from the app's point of view.
 *
 * Derived from the clock and the published content, never authored — there is no field to remember
 * to flip, because the one weekend anybody would forget is the weekend they are at the festival
 * rather than at a laptop.
 *
 * Deliberately wider than the FestivalDays it surrounds. A FestivalDay says when content happens; a
 * phase says where the user's head is. At 08:00 on the opening Friday the festival is already [LIVE]
 * even though nothing opens until the afternoon, and [LIVE] runs to the morning after the last day
 * rather than ending at 23:01 on the Sunday while people are still on the beach.
 */
enum class Phase {
    /** The default, and where the year spends most of its time. */
    OFF_SEASON,

    /** The programme exists and has been published. One job: send people to it. */
    ANNOUNCED,

    /** The last week. The only time anyone will realistically build their Plan. */
    APPROACHING,

    /** Midnight on day one to the morning after the last day, spanning the gaps between days. */
    LIVE,

    /** The six weeks after, for a thank-you and the closing figures. */
    ENDED,
}
