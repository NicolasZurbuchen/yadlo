package io.nicolaszurbuchen.yadlo.feature.monyadlo.presentation.screen.wishlist

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

data class WishlistGroupUiModel(
    val id: String,
    val name: String,
    val stands: List<WishlistStandUiModel>,
)

/**
 * One Stand.
 *
 * [offering] answers what someone walking the row is asking — "Cuisine libanaise" — and [marks]
 * qualify it. They are joined into one string here rather than kept as a list, because they read as
 * one line under the name and the row has no reason to lay them out individually.
 *
 * No hours and no live state — DECISIONS.md § No opening times on the Wishlist. Whether stands close
 * before the festival does is unknown, and "ouvert maintenant" is too good a claim to invent.
 */
data class WishlistStandUiModel(
    val id: String,
    val name: String,
    val offering: String?,
    val marks: String?,
)
