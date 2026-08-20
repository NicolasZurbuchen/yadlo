package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.StandCardUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import org.jetbrains.compose.resources.StringResource
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.plus_entry_stands_food
import yadlo.shared.generated.resources.plus_entry_stands_makers

/**
 * One half of the stands — the browse side of the pair whose recall side is the Wishlist.
 *
 * [emptyMessage] separates the two ways this list is empty: nothing published at all, and nothing
 * matching the chip that is selected. They are different problems and only one of them is the
 * reader's to fix.
 */
data class StandsUiModel(
    val isLoading: Boolean,
    val title: UiText,
    val chips: List<StandChipUiModel>,
    val stands: List<StandCardUiModel>,
    val emptyMessage: UiText?,
)

/**
 * Which half is being shown, carrying the one string the content cannot supply.
 *
 * A mirror of `StandKind` rather than the domain enum itself, and the mirror earns its keep: the
 * store translates once at construction, so neither the navigation package nor the UiMapper has to
 * name a domain type — and neither is allowed to. It is the last of these left, now that Festival
 * responsable stopped being one page of a parameterised gabarit and became a screen about one thing.
 *
 * The titles are the app's words, not the content's. The Category is called *Restauration*; the
 * entry someone taps when they are hungry says *Nourriture & boissons*.
 */
enum class StandsKindUiModel(
    val title: StringResource,
) {
    FOOD(Res.string.plus_entry_stands_food),
    MAKERS(Res.string.plus_entry_stands_makers),
}

/** [mark] is null on *Tout*, which is the chip that clears the filter rather than a mark of its own. */
data class StandChipUiModel(
    val mark: String?,
    val label: UiText,
    val isSelected: Boolean,
)
