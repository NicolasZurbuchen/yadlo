package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloDietaryMarkUiModel
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloDietaryTagUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.StandCardUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.dietary_all_dairy_free
import yadlo.shared.generated.resources.dietary_all_vegan
import yadlo.shared.generated.resources.stands_filter_all
import yadlo.shared.generated.resources.stands_no_match

/**
 * The 2026 shape of both halves. *Créateurs* is the one worth looking at: it publishes no marks at
 * all, so its chip row is absent rather than showing a lone *Tout* that filters nothing.
 */
private class StandsStateProvider : PreviewParameterProvider<StandsUiModel> {
    override val values =
        sequenceOf(
            StandsUiModel(
                isLoading = true,
                title = UiText.Resource(StandsKindUiModel.FOOD.title),
                chips = emptyList(),
                stands = emptyList(),
                emptyMessage = null,
            ),
            food(),
            makers(),
            StandsUiModel(
                isLoading = false,
                title = UiText.Resource(StandsKindUiModel.FOOD.title),
                chips = chips(selected = "sans gluten"),
                stands = emptyList(),
                emptyMessage = UiText.Resource(Res.string.stands_no_match),
            ),
        )
}

@Preview
@Composable
private fun StandsScreenPreview(
    @PreviewParameter(StandsStateProvider::class) state: StandsUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            StandsScreen(state = state, onBackClick = {}, onMarkClick = {}, onStandClick = {})
        }
    }
}

/**
 * The dark half. The chips are the thing to check here: they sit on the bar's own surface now
 * rather than on the page, so the selected chip's ink has to clear a ground it did not before.
 */
@Preview
@Composable
private fun StandsScreenDarkPreview(
    @PreviewParameter(StandsStateProvider::class) state: StandsUiModel,
) {
    YadloTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            StandsScreen(state = state, onBackClick = {}, onMarkClick = {}, onStandClick = {})
        }
    }
}

private fun chips(selected: String?) =
    listOf(
        StandChipUiModel(
            mark = null,
            label = UiText.Resource(Res.string.stands_filter_all),
            isSelected = selected == null,
        ),
    ) +
        listOf("végan", "bio", "végé", "piquant", "sans gluten").map {
            StandChipUiModel(mark = it, label = UiText.Raw(it), isSelected = it == selected)
        }

private fun food() =
    StandsUiModel(
        isLoading = false,
        title = UiText.Resource(StandsKindUiModel.FOOD.title),
        emptyMessage = null,
        chips = chips(selected = null),
        stands =
            listOf(
                StandCardUiModel(
                    id = "vegan-fabrik",
                    name = "Vegan Fabrik",
                    offering = "Cuisine végétale",
                    imageUrl = "$BANK/stands/vegan-fabrik.webp",
                    dietary =
                        listOf(
                            YadloDietaryTagUiModel(YadloDietaryMarkUiModel.VEGAN, Res.string.dietary_all_vegan),
                            YadloDietaryTagUiModel(YadloDietaryMarkUiModel.DAIRY_FREE, Res.string.dietary_all_dairy_free),
                        ),
                ),
                // Matched by the `végé` chip through one bokit, and showing no mark of its own —
                // the case the stand/item distinction exists for. With no dietary band it is a
                // picture over two lines, which is the shape every Créateurs card has.
                StandCardUiModel(
                    id = "de-lor-bokit",
                    name = "De l'Or Bokit",
                    offering = "Cuisine guadeloupéenne",
                    imageUrl = "$BANK/stands/de-lor-bokit.webp",
                    dietary = emptyList(),
                ),
                // And the stand whose photograph has not arrived: nothing about the card changes
                // but which picture is in the frame — see StandCard.
                StandCardUiModel(
                    id = "guliko",
                    name = "Guliko",
                    offering = "Cuisine géorgienne",
                    imageUrl = null,
                    dietary = emptyList(),
                ),
            ),
    )

private fun makers() =
    StandsUiModel(
        isLoading = false,
        title = UiText.Resource(StandsKindUiModel.MAKERS.title),
        emptyMessage = null,
        // No marks published on either maker, so no chip row at all.
        chips =
            listOf(
                StandChipUiModel(
                    mark = null,
                    label = UiText.Resource(Res.string.stands_filter_all),
                    isSelected = true,
                ),
            ),
        stands =
            listOf(
                StandCardUiModel(
                    id = "la-fanfrelucherie",
                    name = "La Fanfrelucherie",
                    offering = "Costumes de seconde main",
                    imageUrl = "$BANK/stands/la-fanfrelucherie.webp",
                    dietary = emptyList(),
                ),
                StandCardUiModel(
                    id = "les-secrets-de-houna",
                    name = "Les Secrets de Houna",
                    offering = "Huiles précieuses et soins naturels",
                    imageUrl = "$BANK/stands/les-secrets-de-houna.webp",
                    dietary = emptyList(),
                ),
            ),
    )

/** Where the published bank lives, so a preview names the file the running app actually fetches. */
private const val BANK = "https://nicolaszurbuchen.github.io/yadlo/shared/images"
