package io.nicolaszurbuchen.yadlo.feature.plus.domain.model

/**
 * Which of the shared text pages is being asked for.
 *
 * An enum rather than a String because it crosses a navigation boundary and back: the destination
 * carries it through process death, and a typo would otherwise become an empty screen at the far
 * end rather than a compile error at this one.
 *
 * **One value at the moment**, since *Réseaux sociaux* stopped being a page when the networks moved
 * to the foot of the tab. The shape stays because the page it describes is the shape most of the
 * remaining unpublished sections will take — adding one is a branch in the use case and a title
 * string, against a screen, a store, a navigator method and a destination for the alternative.
 */
enum class PlusPageId {
    /** *Festival responsable* — the charters the association has signed. */
    RESPONSIBLE,
}
