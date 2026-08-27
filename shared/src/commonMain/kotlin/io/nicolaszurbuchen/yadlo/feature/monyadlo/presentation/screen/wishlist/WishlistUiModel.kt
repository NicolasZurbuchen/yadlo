package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.StandCardUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *À essayer* — the saved Stands, grouped by Category.
 *
 * [emptyMessage] is non-null exactly when [groups] is empty and the screen has loaded, and it points
 * at Plus rather than offering a `+`: Mon Yadlo recalls, it never browses (DECISIONS.md § The `+` in
 * the Wishlist).
 */
data class WishlistUiModel(
    val isLoading: Boolean,
    val groups: List<WishlistGroupUiModel>,
    val emptyMessage: UiText?,
)

/**
 * [stands] are the same [StandCardUiModel] the browse lists draw. *À essayer* is what you kept from
 * Plus › Nourriture & boissons, and a Stand that changed shape between the screen it was saved on
 * and the screen it was saved to would read as a different object.
 *
 * No hours and no live state on any of them — DECISIONS.md § No opening times on the Wishlist.
 * Whether stands close before the festival does is unknown, and "ouvert maintenant" is too good a
 * claim to invent.
 */
data class WishlistGroupUiModel(
    val id: String,
    val name: String,
    val stands: List<StandCardUiModel>,
)
