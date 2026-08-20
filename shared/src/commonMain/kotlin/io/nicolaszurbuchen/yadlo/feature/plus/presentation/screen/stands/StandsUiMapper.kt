package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloDietaryMarkUiModel
import io.nicolaszurbuchen.yadlo.app.design.uimodel.toDietaryTags
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.StandCardUiModel
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
            // Every selected mark, not any of them. See StandsState for why the union is the
            // dangerous half of that choice.
            .filter { stand -> stand.dietary.keys.containsAll(selectedMarks) }
            .map { stand ->
                StandCardUiModel(
                    id = stand.id,
                    name = stand.name,
                    offering = stand.offering,
                    imageUrl = stand.imageUrl,
                    dietary = stand.dietary.toDietaryTags(),
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
                    isSelected = selectedMarks.isEmpty(),
                ),
            ) +
                // The app's word for the content's id, and the same word on every screen. A chip
                // for a mark this build has no glyph for is dropped rather than labelled with a
                // slug — see YadloDietaryMarkUiModel.forId.
                loaded.marks.mapNotNull { mark ->
                    YadloDietaryMarkUiModel.forId(mark)?.let {
                        StandChipUiModel(
                            mark = mark,
                            label = UiText.Resource(it.label),
                            isSelected = mark in selectedMarks,
                        )
                    }
                },
        stands = filtered,
        emptyMessage =
            when {
                filtered.isNotEmpty() -> null

                // Nothing matched the chips the reader chose, versus nothing published at all. Only
                // one of the two is something they can do anything about.
                selectedMarks.isNotEmpty() -> UiText.Resource(Res.string.stands_no_match)

                else -> UiText.Resource(Res.string.stands_empty)
            },
    )
}
