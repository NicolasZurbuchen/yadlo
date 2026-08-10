package io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.screen.detail.DetailRoute
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.screen.detail.DetailViewModel
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.screen.main.MainRoute
import io.nicolaszurbuchen.yadlo.infra.navigation.NavKeyHandler
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class PokemonExplorerNavKeyHandler(
    private val navigator: PokemonExplorerNavigator,
) : NavKeyHandler {
    override fun EntryProviderScope<NavKey>.registerEntries() {
        entry<MainDestination> {
            MainRoute(onNavigateToDetail = { historyId -> navigator.navigateToDetail(historyId) })
        }

        entry<DetailDestination> { destination ->
            DetailRoute(
                onNavigateBack = { navigator.navigateBack() },
                viewModel = koinViewModel<DetailViewModel>(parameters = { parametersOf(destination.historyId) }),
            )
        }
    }
}
