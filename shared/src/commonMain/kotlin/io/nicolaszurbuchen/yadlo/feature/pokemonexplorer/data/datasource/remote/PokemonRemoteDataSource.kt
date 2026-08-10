package io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.data.datasource.remote

import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.data.datasource.remote.dto.PokemonDto

interface PokemonRemoteDataSource {
    suspend fun fetchPokemon(id: Int): PokemonDto
}
