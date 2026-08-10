package io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.data.datasource.remote

import io.nicolaszurbuchen.yadlo.common.error.AppError
import io.nicolaszurbuchen.yadlo.common.error.AppException
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.data.datasource.remote.api.PokemonApi
import io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.data.datasource.remote.dto.PokemonDto
import kotlinx.coroutines.CancellationException

class PokemonRemoteDataSourceImpl(
    private val api: PokemonApi,
) : PokemonRemoteDataSource {
    override suspend fun fetchPokemon(id: Int): PokemonDto =
        try {
            api.getPokemon(id)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            throw AppException(AppError.PokemonExplorer.FetchFailed)
        }
}
