package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.page

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

/**
 * Both shapes the gabarit takes: a titled section with prose and a link, and an untitled one that
 * is nothing but links. If those two do not both read well, the entries sharing this screen were
 * wrong to share it.
 *
 * Only one page routes here today — *Réseaux sociaux* became the footer of the tab — so the second
 * is drawn under the same title. It stays because the untitled-section shape is the one the
 * remaining unpublished sections will arrive in, and it is the shape that breaks first.
 */
private class PageStateProvider : PreviewParameterProvider<PageUiModel> {
    override val values =
        sequenceOf(
            responsible(),
            untitledSections(),
        )
}

@Preview
@Composable
private fun PageScreenPreview(
    @PreviewParameter(PageStateProvider::class) state: PageUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            PageScreen(state = state, onBackClick = {}, onLinkClick = {})
        }
    }
}

private fun responsible() =
    PageUiModel(
        isLoading = false,
        title = UiText.Resource(PageKindUiModel.RESPONSIBLE.title),
        emptyMessage = null,
        sections =
            listOf(
                PageSectionUiModel(
                    id = "festiplus",
                    title = "FestiPlus",
                    body =
                        "Yadlo est membre de FestiPlus, une charte vaudoise qui promeut le bien-être en " +
                            "festival et la prévention des risques liés à l'alcool.",
                    links =
                        listOf(
                            PageLinkUiModel(
                                id = "festiplus",
                                label = "FestiPlus",
                                sublabel = null,
                                url = "https://festiplus.ch/",
                            ),
                        ),
                ),
            ),
    )

private fun untitledSections() =
    PageUiModel(
        isLoading = false,
        title = UiText.Resource(PageKindUiModel.RESPONSIBLE.title),
        emptyMessage = null,
        sections =
            listOf(
                PageSectionUiModel(
                    id = "chartes",
                    title = null,
                    body = null,
                    links =
                        listOf(
                            PageLinkUiModel(id = "festiplus", label = "FestiPlus", sublabel = null, url = "https://a"),
                            PageLinkUiModel(id = "lac", label = "Charte du lac", sublabel = null, url = "https://b"),
                            PageLinkUiModel(id = "vaud", label = "Charte vaudoise", sublabel = null, url = "https://c"),
                        ),
                ),
            ),
    )
