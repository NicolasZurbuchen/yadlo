package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.design.uimodel.YadloFigureUiModel
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_figures_caveat

/**
 * The skeleton and the page, and three figures is the count that matters: two rows of two with the
 * last cell empty is the only arrangement of the grid that can go wrong.
 */
private class StoryScreenStateProvider : PreviewParameterProvider<StoryUiModel> {
    override val values =
        sequenceOf(
            StoryUiModel(
                isLoading = true,
                body = null,
                passageTitle = null,
                passageBody = null,
                figures = emptyList(),
                figuresCaveat = null,
            ),
            published(),
        )

    /** With the caveat showing: 2026 has not happened, so any figure on this screen is a past year's. */
    private fun published() =
        StoryUiModel(
            isLoading = false,
            body =
                "Yadlo est né en 2015 d'un petit groupe d'amis passionnés de windsurf, de surf et de sports " +
                    "nautiques à Préverenges.\n\nLe festival vit aujourd'hui grâce à ses bénévoles et ses partenaires.",
            passageTitle = "Une journée à Yadlo",
            passageBody = "Tôt le matin, les paddles glissent sur une eau encore calme.",
            figures =
                listOf(
                    YadloFigureUiModel(id = "visiteurs", value = "6000", label = "visiteurs"),
                    YadloFigureUiModel(id = "benevoles", value = "160", label = "bénévoles"),
                    YadloFigureUiModel(id = "biere", value = "3200", label = "litres de bière"),
                ),
            figuresCaveat = UiText.Resource(Res.string.home_figures_caveat),
        )
}

@PreviewThemes
@Composable
private fun StoryScreenPreview(
    @PreviewParameter(StoryScreenStateProvider::class) state: StoryUiModel,
) {
    YadloPreview {
        StoryScreen(state = state, onBackClick = {})
    }
}
