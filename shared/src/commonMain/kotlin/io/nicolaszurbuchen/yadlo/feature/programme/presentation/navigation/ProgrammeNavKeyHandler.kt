package io.nicolaszurbuchen.yadlo.feature.programme.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.programme.presentation.screen.programme.ProgrammeRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler

class ProgrammeNavKeyHandler(
    private val navigator: ProgrammeNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        entry<ProgrammeDestination> {
            ProgrammeRoute(onNavigateToHappening = { id -> navigator.navigateToHappening(id) })
        }
    }
}
