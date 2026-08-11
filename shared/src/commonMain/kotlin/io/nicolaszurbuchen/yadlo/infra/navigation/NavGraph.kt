package io.nicolaszurbuchen.yadlo.infra.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.getKoin

/**
 * Renders one back stack. It does not own one, and it knows nothing about tabs — the caller
 * decides which stack is visible, which is what lets four tabs each keep their own depth while
 * sharing a single set of registered entries.
 */
@Composable
fun NavGraph(
    backStack: NavBackStack<NavKey>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val handlers = getKoin().getAll<NavKeyHandler>()

    NavDisplay(
        backStack = backStack,
        onBack = { onBack() },
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        entryProvider =
            entryProvider {
                handlers.forEach { handler ->
                    with(handler) { registerEntries() }
                }
            },
        modifier = modifier,
    )
}
