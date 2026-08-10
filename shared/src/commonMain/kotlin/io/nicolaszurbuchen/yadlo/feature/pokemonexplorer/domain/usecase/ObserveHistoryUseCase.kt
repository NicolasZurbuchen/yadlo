package io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.domain.usecase

import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.domain.model.Pokemon
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.domain.repository.PokemonExplorerRepository
import kotlinx.coroutines.flow.Flow

class ObserveHistoryUseCase(
    private val repository: PokemonExplorerRepository,
) {
    operator fun invoke(): Flow<List<Pokemon>> = repository.observeHistory()
}
