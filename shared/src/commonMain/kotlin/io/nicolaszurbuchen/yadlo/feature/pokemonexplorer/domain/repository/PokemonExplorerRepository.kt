package io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.domain.repository

import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow

interface PokemonExplorerRepository {
    suspend fun fetchRandomPokemon(): Pokemon

    fun observeHistory(): Flow<List<Pokemon>>

    suspend fun clearHistory()

    suspend fun getById(historyId: Long): Pokemon?
}
