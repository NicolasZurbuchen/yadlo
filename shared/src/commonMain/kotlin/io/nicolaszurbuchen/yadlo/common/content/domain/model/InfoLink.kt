package io.nicolaszurbuchen.yadlo.common.content.domain.model

/**
 * A link on a practical-information screen, written out for a reader rather than typed for an icon.
 *
 * Distinct from [Link], and the difference is which end names it. A [Link] is an artist's Spotify:
 * the app knows what Spotify is and draws the mark, so the content only supplies a `type`. This one
 * is *Horaires ligne 701 · PDF · MBC* — nothing about it is derivable, so the content writes both
 * lines and the app renders what it is given.
 *
 * [sublabel] is null when the label says it all.
 */
data class InfoLink(
    val id: String,
    val label: String,
    val sublabel: String?,
    val url: String,
)
