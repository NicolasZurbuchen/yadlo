package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.stands_empty
import yadlo.shared.generated.resources.stands_filter_all
import yadlo.shared.generated.resources.stands_no_match

fun StandsState.toUiModel(): StandsUiModel {
    val title = UiText.Resource(kind.title)

    val loaded =
        directory ?: return StandsUiModel(
            isLoading = true,
            title = title,
            chips = emptyList(),
            stands = emptyList(),
            emptyMessage = null,
        )

    val filtered =
        loaded.stands
            .filter { stand -> selectedMark == null || selectedMark in stand.dietaryMatches }
            .map { stand ->
                StandUiModel(
                    id = stand.id,
                    name = stand.name,
                    offering = stand.offering,
                    // The fiche's separator, so a stand reads the same in all three places it
                    // appears.
                    marks = stand.marks.joinToString(" · ").ifEmpty { null },
                )
            }

    return StandsUiModel(
        isLoading = false,
        title = title,
        chips =
            listOf(
                StandChipUiModel(
                    mark = null,
                    label = UiText.Resource(Res.string.stands_filter_all),
                    isSelected = selectedMark == null,
                ),
            ) +
                loaded.marks.map { mark ->
                    StandChipUiModel(
                        // The content's own word, unchanged: `végé` and `sans gluten` are how the
                        // festival writes them and how a menu board writes them.
                        mark = mark,
                        label = UiText.Raw(mark),
                        isSelected = mark == selectedMark,
                    )
                },
        stands = filtered,
        emptyMessage =
            when {
                filtered.isNotEmpty() -> null

                // Nothing matched a chip the reader chose, versus nothing published at all. Only one
                // of the two is something they can do anything about.
                selectedMark != null -> UiText.Resource(Res.string.stands_no_match)

                else -> UiText.Resource(Res.string.stands_empty)
            },
    )
}
