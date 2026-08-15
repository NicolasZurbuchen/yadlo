package io.nicolaszurbuchen.yadlo.feature.happening.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningRoute
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningViewModel
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class HappeningNavKeyHandler(
    private val navigator: HappeningNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        // The id reaches the Store through the ViewModel rather than through the Route, because a
        // Route may only take lambdas, a Modifier or a ViewModel. The destination survives process
        // death, so a restored fiche rebuilds against the same Happening.
        entry<HappeningDestination> { destination ->
            HappeningRoute(
                onNavigateBack = { navigator.navigateBack() },
                viewModel = koinViewModel<HappeningViewModel>(parameters = { parametersOf(destination.happeningId) }),
            )
        }
    }
}
