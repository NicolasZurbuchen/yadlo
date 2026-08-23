package io.nicolaszurbuchen.yadlo.feature.search.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * One destination, pushed onto whichever tab's stack is showing.
 *
 * **Not a tab, and not a stack of its own.** Search is reached from three tabs and from Accueil, and
 * pushing it where it was opened from is what makes backing out of it land where the reader was —
 * the same rule the fiche follows when it is opened from Programme and from Plus. A tab switch would
 * strand them somewhere they never chose.
 */
@Serializable
data object SearchDestination : NavKey
