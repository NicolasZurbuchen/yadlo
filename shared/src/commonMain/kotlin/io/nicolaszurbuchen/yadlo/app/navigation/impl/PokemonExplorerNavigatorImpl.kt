package io.nicolaszurbuchen.yadlo.app.navigation.impl

import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.navigation.DetailDestination
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.navigation.PokemonExplorerNavigator
import io.nicolaszurbuchen.yadlo.infra.navigation.AppNavigator

class PokemonExplorerNavigatorImpl(
    private val appNavigator: AppNavigator,
) : PokemonExplorerNavigator {
    override fun navigateToDetail(historyId: Long) {
        appNavigator.navigateTo(DetailDestination(historyId))
    }

    override fun navigateBack() {
        appNavigator.navigateBack()
    }
}
