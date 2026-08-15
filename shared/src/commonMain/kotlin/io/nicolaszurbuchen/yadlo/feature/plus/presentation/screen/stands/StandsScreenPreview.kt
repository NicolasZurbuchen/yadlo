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
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.stands_filter_all
import yadlo.shared.generated.resources.stands_no_match

/**
 * The 2026 shape: two categories, and a stand with marks beside three without. The filtered-to-
 * nothing state is the third, because it is the one a reader can act on and the one that reads
 * wrong if it says "no stands published".
 */
private class StandsStateProvider : PreviewParameterProvider<StandsUiModel> {
    override val values =
        sequenceOf(
            StandsUiModel(isLoading = true, chips = emptyList(), groups = emptyList(), emptyMessage = null),
            listed(),
            StandsUiModel(
                isLoading = false,
                chips = chips(selected = "sans gluten"),
                groups = emptyList(),
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

private fun listed() =
    StandsUiModel(
        isLoading = false,
        emptyMessage = null,
        chips = chips(selected = null),
        groups =
            listOf(
                StandGroupUiModel(
                    id = "restauration",
                    name = "Restauration",
                    stands =
                        listOf(
                            StandUiModel(
                                id = "vegan-fabrik",
                                name = "Vegan Fabrik",
                                offering = "Cuisine végétale",
                                marks = "végan · bio",
                            ),
                            // Matched by the `végé` chip through one bokit, and showing no mark of
                            // its own — the case the stand/item distinction exists for.
                            StandUiModel(
                                id = "de-lor-bokit",
                                name = "De l'Or Bokit",
                                offering = "Cuisine guadeloupéenne",
                                marks = null,
                            ),
                            StandUiModel(id = "guliko", name = "Guliko", offering = "Cuisine géorgienne", marks = null),
                        ),
                ),
                StandGroupUiModel(
                    id = "createurs",
                    name = "Créateurs",
                    stands =
                        listOf(
                            StandUiModel(
                                id = "la-fanfrelucherie",
                                name = "La Fanfrelucherie",
                                offering = "Costumes de seconde main",
                                marks = null,
                            ),
                        ),
                ),
            ),
    )
