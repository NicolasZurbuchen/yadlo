package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *Nourriture & boissons* — the browse half of the pair whose recall half is the Wishlist.
 *
 * [emptyMessage] separates the two ways this list is empty: nothing published at all, and nothing
 * matching the chip that is selected. They are different problems and only one of them is the
 * reader's to fix.
 */
data class StandsUiModel(
    val isLoading: Boolean,
    val chips: List<StandChipUiModel>,
    val groups: List<StandGroupUiModel>,
    val emptyMessage: UiText?,
)

/** [mark] is null on *Tout*, which is the chip that clears the filter rather than a mark of its own. */
data class StandChipUiModel(
    val mark: String?,
    val label: UiText,
    val isSelected: Boolean,
)

data class StandGroupUiModel(
    val id: String,
    val name: String,
    val stands: List<StandUiModel>,
)

/**
 * One Stand.
 *
 * [marks] are the Stand's own and nothing more, even when the filter matched this row through a
 * single dish. Widening them here would turn "sells one vegan bokit" into "is vegan", which is the
 * exact claim SCHEMA.md keeps the two levels apart to avoid.
 *
 * **No hours.** Not one of the stands publishes any — see content/GAPS.md — and a time on this row
 * would be invented. It is the single most useful thing the association could send.
 */
data class StandUiModel(
    val id: String,
    val name: String,
    val offering: String?,
    val marks: String?,
)
