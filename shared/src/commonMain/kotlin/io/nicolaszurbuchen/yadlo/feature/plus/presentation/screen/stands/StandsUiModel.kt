package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.StandCardUiModel
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
 * navigation package may not name a domain type, so this is what the NavKeyHandler hands to Koin,
 * and the Store translates once at construction. The State has held `StandKind` since then.
 *
 * **It goes no further than that hand-off, and that is deliberate.** It used to be an argument on
 * `StandsDestination`, which made a presentation enum’s constant names the persisted format of the
 * back stack — renaming one is an ordinary refactor and would have broken a restore after process
 * death, with nothing to catch it. There are two keys now, both `data object`, and this reaches
 * the Store in memory instead. Nothing writes it down.
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
