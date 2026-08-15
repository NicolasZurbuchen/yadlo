package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

/**
 * *Nourriture & boissons*, whole: the Stands grouped by Category, and the dietary marks worth
 * offering as a filter.
 *
 * [marks] is derived from the listing rather than declared in Kotlin, so a chip is never shown that
 * matches nothing, and a mark the content adds appears without an app release. Deciding here which
 * of the six published marks count as "dietary" would be duplicating a content decision in code —
 * `piquant` is exactly as much a reason to avoid a stand as `sans gluten` is a reason to choose it.
 */
data class StandDirectory(
    val groups: List<StandGroup>,
    val marks: List<String>,
)
