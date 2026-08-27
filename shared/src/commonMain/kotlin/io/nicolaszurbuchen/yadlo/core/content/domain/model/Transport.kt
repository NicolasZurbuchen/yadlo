package io.nicolaszurbuchen.yadlo.core.content.domain.model

/**
 * Every published way of getting to the beach and away from it again.
 *
 * Ordered as the content authors it, which is chronological rather than alphabetical — coming
 * before going home, the way the page is read before leaving the house. The cost is real and was
 * accepted: at two in the morning the last bus takes a little scrolling to find.
 */
data class Transport(
    val modes: List<TransportMode>,
    val provenance: Provenance,
)
