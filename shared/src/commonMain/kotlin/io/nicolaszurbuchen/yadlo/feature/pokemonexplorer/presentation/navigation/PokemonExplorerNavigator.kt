package io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.navigation

interface PokemonExplorerNavigator {
    fun navigateToDetail(historyId: Long)

    fun navigateBack()
}
