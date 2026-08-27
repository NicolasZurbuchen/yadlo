package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.responsible

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * The skeleton and the page, which is all this screen has now that it is about one thing.
 *
 * The second state used to be an untitled section full of links — the other shape the gabarit could
 * take. There is no other shape any more, and drawing one under the *Festival responsable* title
 * was a preview of a screen that does not exist.
 */
private class ResponsibleScreenStateProvider : PreviewParameterProvider<ResponsibleUiModel> {
    override val values =
        sequenceOf(
            ResponsibleUiModel(isLoading = true, sections = emptyList(), emptyMessage = null),
            published(),
        )

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
}

@PreviewThemes
@Composable
private fun ResponsibleScreenPreview(
    @PreviewParameter(ResponsibleScreenStateProvider::class) state: ResponsibleUiModel,
) {
    YadloPreview {
        ResponsibleScreen(state = state, onBackClick = {}, onLinkClick = {})
    }
}
