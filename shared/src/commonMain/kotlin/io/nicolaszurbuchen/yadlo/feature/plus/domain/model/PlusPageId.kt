package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

/**
 * Which of the shared text pages is being asked for.
 *
 * An enum rather than a String because it crosses a navigation boundary and back: the destination
 * carries it through process death, and a typo would otherwise become an empty screen at the far
 * end rather than a compile error at this one.
 */
enum class PlusPageId {
    /** *Festival responsable* — the charters the association has signed. */
    RESPONSIBLE,

    /** *Réseaux sociaux* — where the festival actually is for 361 days of the year. */
    SOCIAL,
}
