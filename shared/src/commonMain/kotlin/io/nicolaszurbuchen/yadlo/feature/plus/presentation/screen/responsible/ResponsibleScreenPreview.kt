package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

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

/**
 * The skeleton and the page, which is all this screen has now that it is about one thing.
 *
 * The second state used to be an untitled section full of links — the other shape the gabarit could
 * take. There is no other shape any more, and drawing one under the *Festival responsable* title
 * was a preview of a screen that does not exist.
 */
private class ResponsibleStateProvider : PreviewParameterProvider<ResponsibleUiModel> {
    override val values =
        sequenceOf(
            ResponsibleUiModel(isLoading = true, sections = emptyList(), emptyMessage = null),
            published(),
        )
}

@Preview
@Composable
private fun ResponsibleScreenPreview(
    @PreviewParameter(ResponsibleStateProvider::class) state: ResponsibleUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            ResponsibleScreen(state = state, onBackClick = {}, onLinkClick = {})
        }
    }
}

@Preview
@Composable
private fun ResponsibleScreenDarkPreview(
    @PreviewParameter(ResponsibleStateProvider::class) state: ResponsibleUiModel,
) {
    YadloTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            ResponsibleScreen(state = state, onBackClick = {}, onLinkClick = {})
        }
    }
}

/** FestiPlus is the one charter published today, and one section is the ordinary case. */
private fun published() =
    ResponsibleUiModel(
        isLoading = false,
        emptyMessage = null,
        sections =
            listOf(
                ResponsibleSectionUiModel(
                    id = "festiplus",
                    title = "FestiPlus",
                    body =
                        "Yadlo est membre de FestiPlus, une charte vaudoise qui promeut le bien-être en " +
                            "festival et la prévention des risques liés à l'alcool.",
                    links =
                        listOf(
                            ResponsibleLinkUiModel(
                                id = "festiplus",
                                label = "FestiPlus",
                                sublabel = null,
                                url = "https://festiplus.ch/",
                            ),
                        ),
                ),
            ),
    )
