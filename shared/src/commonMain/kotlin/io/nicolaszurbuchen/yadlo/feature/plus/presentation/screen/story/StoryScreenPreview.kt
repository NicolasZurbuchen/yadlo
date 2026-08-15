package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_figures_caveat

private class StoryStateProvider : PreviewParameterProvider<StoryUiModel> {
    override val values =
        sequenceOf(
            StoryUiModel(
                isLoading = true,
                body = null,
                passageTitle = null,
                passageBody = null,
                figures = emptyList(),
                figuresCaveat = null,
                emptyMessage = null,
            ),
            published(),
        )
}

@Preview
@Composable
private fun StoryScreenPreview(
    @PreviewParameter(StoryStateProvider::class) state: StoryUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            StoryScreen(state = state, onBackClick = {})
        }
    }
}

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
                StoryFigureUiModel(id = "visiteurs", value = "6000", label = "visiteurs"),
                StoryFigureUiModel(id = "benevoles", value = "160", label = "bénévoles"),
                StoryFigureUiModel(id = "biere", value = "3200", label = "litres de bière"),
            ),
        figuresCaveat = UiText.Resource(Res.string.home_figures_caveat),
        emptyMessage = null,
    )
