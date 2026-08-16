package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Everywhere the Mon Yadlo tab can go. */
@Serializable
sealed interface MonYadloDestination : NavKey

/** The tab root: the timeline, with the Wishlist tile above it. */
@Serializable
internal data object MonYadloMainDestination : MonYadloDestination

/**
 * Pushed onto Mon Yadlo's own stack rather than given a tab. It is the other half of one tab, and
 * DECISIONS.md § Two verbs puts one full-width tile in front of it — not a segmented control, which
 * would make the Plan and the Wishlist look like two views of the same thing.
 */
@Serializable
internal data object WishlistDestination : MonYadloDestination
