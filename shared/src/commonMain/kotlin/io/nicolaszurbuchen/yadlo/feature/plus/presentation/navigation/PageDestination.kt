package io.nicolaszurbuchen.yadlo.feature.plus.presentation.navigation

import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page.PageKind
import kotlinx.serialization.Serializable

/**
 * The shared text page, and which of them it is.
 *
 * One destination with a parameter rather than one per page, for the same reason there is one
 * screen: *Festival responsable* and *Réseaux sociaux* are a title, some prose and some links, and
 * the next entry the association publishes will be too. The kind survives process death, so a
 * restored back stack rebuilds the page it was on.
 *
 * It carries [PageKind] rather than the domain's own `PlusPageId` because navigation is
 * presentation, and presentation may not import from domain. The store translates the two once, at
 * the only point that has business knowing both.
 */
@Serializable
data class PageDestination(
    val kind: PageKind,
) : NavKey
