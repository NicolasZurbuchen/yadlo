package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_figures_caveat
import yadlo.shared.generated.resources.story_empty

fun StoryState.toUiModel(): StoryUiModel {
    if (!hasLoaded) {
        return StoryUiModel(
            isLoading = true,
            body = null,
            passageTitle = null,
            passageBody = null,
            figures = emptyList(),
            figuresCaveat = null,
            emptyMessage = null,
        )
    }

    val loaded =
        page ?: return StoryUiModel(
            isLoading = false,
            body = null,
            passageTitle = null,
            passageBody = null,
            figures = emptyList(),
            figuresCaveat = null,
            emptyMessage = UiText.Resource(Res.string.story_empty),
        )

    return StoryUiModel(
        isLoading = false,
        body = loaded.body,
        passageTitle = loaded.passageTitle,
        passageBody = loaded.passageBody,
        figures = loaded.figures.map { StoryFigureUiModel(id = it.id, value = it.value, label = it.label) },
        // The same caveat Accueil prints under the same numbers, from the same string, because it is
        // the same claim being made in two places.
        figuresCaveat =
            UiText
                .Resource(Res.string.home_figures_caveat)
                .takeIf { loaded.figures.isNotEmpty() && !loaded.figuresAreConfirmed },
        emptyMessage = null,
    )
}
