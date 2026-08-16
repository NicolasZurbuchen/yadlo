package io.nicolaszurbuchen.yadlo.feature.home.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** Everywhere the Accueil tab can go. */
@Serializable
sealed interface HomeDestination : NavKey

/** The tab root. */
@Serializable
internal data object HomeMainDestination : HomeDestination

/**
 * The full annonce feed. Pushed onto the Accueil stack rather than given a tab: it is reached from
 * one place, and back returns to the block it was opened from.
 */
@Serializable
internal data object AnnouncementsDestination : HomeDestination
