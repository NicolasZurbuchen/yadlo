package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import io.nicolaszurbuchen.yadlo.design.uimodel.YadloFigureUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_figures_caveat

fun StoryState.toUiModel(): StoryUiModel {
    val loaded =
        page ?: return StoryUiModel(
            isLoading = true,
            body = null,
            passageTitle = null,
            passageBody = null,
            figures = emptyList(),
            figuresCaveat = null,
        )

    return StoryUiModel(
        isLoading = false,
        body = loaded.body,
        passageTitle = loaded.passageTitle,
        passageBody = loaded.passageBody,
        figures = loaded.figures.map { YadloFigureUiModel(id = it.id, value = it.value, label = it.label) },
        // The same caveat Accueil prints under the same numbers, from the same string, because it is
        // the same claim being made in two places.
        figuresCaveat =
            UiText
                .Resource(Res.string.home_figures_caveat)
                .takeIf { loaded.figures.isNotEmpty() && !loaded.figuresAreConfirmed },
    )
}
