package io.nicolaszurbuchen.yadlo.infra.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay

/**
 * Renders already-decorated entries. It owns no stack and knows nothing about tabs — the caller
 * decides which set of entries is visible, which is what lets four tabs each keep their own
 * history while sharing one display.
 *
 * Entries come in rather than a back stack because a display that is handed a different stack has
 * no way to swap the decorator state that belongs to it. See [rememberNavEntries].
 */
@Composable
fun NavGraph(
    entries: List<NavEntry<NavKey>>,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        entries = entries,
        onBack = { onBack() },
        modifier = modifier,
    )
}
