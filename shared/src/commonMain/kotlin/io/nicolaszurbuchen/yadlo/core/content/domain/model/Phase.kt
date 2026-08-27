package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * Where the year is, from the app's point of view. Derived from the clock and the published
 * content, never authored, and deliberately wider than the FestivalDays it surrounds: a
 * FestivalDay says when content happens, a Phase says where the user's head is.
 */
enum class Phase {
    OFF_SEASON,

    /** The programme exists and has been published. */
    ANNOUNCED,

    /** The last week — the only time anyone realistically builds their Plan. */
    APPROACHING,

    /** Midnight on day one to the morning after the last day, spanning the gaps between days. */
    LIVE,

    /** The six weeks after, for a thank-you and the closing figures. */
    ENDED,
}
