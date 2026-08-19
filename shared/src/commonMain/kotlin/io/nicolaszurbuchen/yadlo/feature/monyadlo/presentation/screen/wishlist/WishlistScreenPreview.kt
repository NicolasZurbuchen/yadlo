package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

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
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.dietary_all_dairy_free
import yadlo.shared.generated.resources.dietary_all_vegan
import yadlo.shared.generated.resources.wishlist_empty

/**
 * Two groups, because two is what the 2026 content can produce — every Stand it declares is either
 * `restauration` or `createurs`. A stand with no marks sits beside one with two, which is the pair
 * worth looking at: the row has to hold its shape when the middle line is missing.
 */
private class WishlistStateProvider : PreviewParameterProvider<WishlistUiModel> {
    override val values =
        sequenceOf(
            WishlistUiModel(isLoading = true, groups = emptyList(), emptyMessage = null),
            kept(),
            WishlistUiModel(
                isLoading = false,
                groups = emptyList(),
                emptyMessage = UiText.Resource(Res.string.wishlist_empty),
            ),
        )
}

@Preview
@Composable
private fun WishlistScreenPreview(
    @PreviewParameter(WishlistStateProvider::class) state: WishlistUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            WishlistScreen(state = state, onBackClick = {}, onStandClick = {})
        }
    }
}

private fun kept() =
    WishlistUiModel(
        isLoading = false,
        emptyMessage = null,
        groups =
            listOf(
                WishlistGroupUiModel(
                    id = "restauration",
                    name = "Restauration",
                    stands =
                        listOf(
                            WishlistStandUiModel(
                                id = "vegan-fabrik",
                                name = "Vegan Fabrik",
                                offering = "Cuisine végétale",
                                dietary =
                                    listOf(
                                        YadloDietaryTagUiModel(YadloDietaryMarkUiModel.VEGAN, Res.string.dietary_all_vegan),
                                        YadloDietaryTagUiModel(
                                            YadloDietaryMarkUiModel.DAIRY_FREE,
                                            Res.string.dietary_all_dairy_free,
                                        ),
                                    ),
                            ),
                            WishlistStandUiModel(
                                id = "guliko",
                                name = "Guliko",
                                offering = "Cuisine géorgienne",
                                dietary = emptyList(),
                            ),
                        ),
                ),
                WishlistGroupUiModel(
                    id = "createurs",
                    name = "Créateurs",
                    stands =
                        listOf(
                            WishlistStandUiModel(
                                id = "la-fanfrelucherie",
                                name = "La Fanfrelucherie",
                                offering = null,
                                dietary = emptyList(),
                            ),
                        ),
                ),
            ),
    )
