package io.nicolaszurbuchen.yadlo.infra.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import org.koin.compose.getKoin

/**
 * Builds the decorated entries for one back stack.
 *
 * **Call this once per back stack, never once for several.** `rememberDecoratedNavEntries`
 * represents a single back stack, and its decorators hold that stack's state — the saveable state
 * of each screen and the ViewModel store scoped to each entry. Sharing one call across the tabs
 * and swapping which stack it is handed makes those decorators describe a stack that is no longer
 * on screen, which is what showed one tab's content while another tab's stack was the live one.
 *
 * Each call site is its own composition slot, so calling this four times gives four independent
 * sets of decorator state — which is exactly what "each tab keeps its own history" means.
 */
@Composable
fun rememberNavEntries(backStack: NavBackStack<NavKey>): List<NavEntry<NavKey>> {
    val handlers = getKoin().getAll<NavKeyHandler>()

    val decorators =
        listOf<NavEntryDecorator<NavKey>>(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        )

    return rememberDecoratedNavEntries(
        backStack = backStack,
        entryDecorators = decorators,
        entryProvider =
            entryProvider {
                handlers.forEach { handler ->
                    with(handler) { registerEntries() }
                }
            },
    )
}
