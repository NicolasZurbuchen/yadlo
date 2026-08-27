package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.StandCardUiModel
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloDietaryMarkUiModel
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloDietaryTagUiModel
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes
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
private class WishlistScreenStateProvider : PreviewParameterProvider<WishlistUiModel> {
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
                                StandCardUiModel(
                                    id = "vegan-fabrik",
                                    name = "Vegan Fabrik",
                                    offering = "Cuisine végétale",
                                    imageUrl = "$BANK/stands/vegan-fabrik.webp",
                                    dietary =
                                        listOf(
                                            YadloDietaryTagUiModel(YadloDietaryMarkUiModel.VEGAN, Res.string.dietary_all_vegan),
                                            YadloDietaryTagUiModel(
                                                YadloDietaryMarkUiModel.DAIRY_FREE,
                                                Res.string.dietary_all_dairy_free,
                                            ),
                                        ),
                                ),
                                StandCardUiModel(
                                    id = "guliko",
                                    name = "Guliko",
                                    offering = "Cuisine géorgienne",
                                    imageUrl = "$BANK/stands/guliko.webp",
                                    dietary = emptyList(),
                                ),
                            ),
                    ),
                    WishlistGroupUiModel(
                        id = "createurs",
                        name = "Créateurs",
                        stands =
                            listOf(
                                StandCardUiModel(
                                    id = "la-fanfrelucherie",
                                    name = "La Fanfrelucherie",
                                    offering = null,
                                    imageUrl = "$BANK/stands/la-fanfrelucherie.webp",
                                    dietary = emptyList(),
                                ),
                            ),
                    ),
                ),
        )

    companion object {
        /** Where the published bank lives, so a preview names the file the running app actually fetches. */
        private const val BANK = "https://nicolaszurbuchen.github.io/yadlo/shared/images"
    }
}

@PreviewThemes
@Composable
private fun WishlistScreenPreview(
    @PreviewParameter(WishlistScreenStateProvider::class) state: WishlistUiModel,
) {
    YadloPreview {
        WishlistScreen(state = state, onBackClick = {}, onStandClick = {})
    }
}
