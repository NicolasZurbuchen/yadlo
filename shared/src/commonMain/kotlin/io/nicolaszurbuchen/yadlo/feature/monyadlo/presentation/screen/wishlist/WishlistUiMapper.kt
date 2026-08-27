package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import io.nicolaszurbuchen.yadlo.core.content.presentation.mapper.toDietaryTags
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.StandCardUiModel
import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.wishlist_empty

fun WishlistState.toUiModel(): WishlistUiModel {
    val loaded =
        groups ?: return WishlistUiModel(
            isLoading = true,
            groups = emptyList(),
            emptyMessage = null,
        )

    return WishlistUiModel(
        isLoading = false,
        groups =
            loaded.map { group ->
                WishlistGroupUiModel(
                    id = group.categoryId,
                    // Written as the content authors it — "Restauration", not RESTAURATION. The
                    // section header's own slot already carries the tracking and the weight.
                    name = group.categoryName,
                    stands =
                        group.stands.map { stand ->
                            StandCardUiModel(
                                id = stand.id,
                                name = stand.name,
                                offering = stand.offering,
                                imageUrl = stand.imageUrl,
                                dietary = stand.dietary.toDietaryTags(),
                            )
                        },
                )
            },
        emptyMessage = if (loaded.isEmpty()) UiText.Resource(Res.string.wishlist_empty) else null,
    )
}
