package io.nicolaszurbuchen.yadlo.feature.pokemonexplorer.presentation.screen.detail

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.pokemon_detail_height_label
import yadlo.shared.generated.resources.pokemon_detail_weight_label

fun DetailState.toUiModel(): DetailUiModel =
    DetailUiModel(
        isLoading = isLoading,
        numberText = pokemon?.let { "#" + it.speciesId.toString().padStart(3, '0') },
        name = pokemon?.name?.replaceFirstChar { char -> char.uppercase() },
        spriteUrl = pokemon?.spriteUrl,
        heightText =
            pokemon?.let {
                UiText.Resource(Res.string.pokemon_detail_height_label, listOf("${it.height / 10.0} m"))
            },
        weightText =
            pokemon?.let {
                UiText.Resource(Res.string.pokemon_detail_weight_label, listOf("${it.weight / 10.0} kg"))
            },
    )
