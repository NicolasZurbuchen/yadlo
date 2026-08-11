package io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler

class HappeningNavKeyHandler(
    private val navigator: HappeningNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        // The destination carries happeningId and it survives process death, but the placeholder
        // screen does not read it yet: a Route may only take lambdas, a Modifier or a ViewModel,
        // so passing an id in means building the full Contract/StoreFactory/ViewModel set. That
        // arrives with the real fiche rather than as scaffolding to delete.
        entry<HappeningDestination> {
            HappeningRoute(onNavigateBack = { navigator.navigateBack() })
        }
    }
}
