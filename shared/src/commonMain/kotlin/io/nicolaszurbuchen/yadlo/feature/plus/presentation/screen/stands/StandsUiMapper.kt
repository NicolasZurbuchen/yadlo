package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.stands

import io.nicolaszurbuchen.yadlo.app.design.uimodel.DietaryMarkUiModel
import io.nicolaszurbuchen.yadlo.app.design.uimodel.toDietaryTags
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
            .filter { stand -> selectedMark == null || selectedMark in stand.dietary }
            .map { stand ->
                StandUiModel(
                    id = stand.id,
                    name = stand.name,
                    offering = stand.offering,
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
                    isSelected = selectedMark == null,
                ),
            ) +
                // The app's word for the content's id, and the same word on every screen. A chip
                // for a mark this build has no glyph for is dropped rather than labelled with a
                // slug — see DietaryMarkUiModel.forId.
                loaded.marks.mapNotNull { mark ->
                    DietaryMarkUiModel.forId(mark)?.let {
                        StandChipUiModel(
                            mark = mark,
                            label = UiText.Resource(it.label),
                            isSelected = mark == selectedMark,
                        )
                    }
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
