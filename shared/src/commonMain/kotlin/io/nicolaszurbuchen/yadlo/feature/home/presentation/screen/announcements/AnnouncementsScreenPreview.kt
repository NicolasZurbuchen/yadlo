package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.announcements

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel.AnnouncementUiModel
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * The three states the feed has: arriving, published, and a festival that has not posted yet.
 *
 * **The published one carries an annonce with no body and one with no link**, because both are
 * shapes the content actually produces — `body` and `url` are optional in the schema — and a
 * preview of three complete annonces is a preview of the case that never goes wrong.
 */
private class AnnouncementsScreenStateProvider : PreviewParameterProvider<AnnouncementsUiModel> {
    override val values =
        sequenceOf(
            AnnouncementsUiModel(isLoading = true, items = emptyList()),
            AnnouncementsUiModel(
                isLoading = false,
                items =
                    listOf(
                        announcement(
                            id = "aftermovie-2026",
                            dateText = "01.08.2026",
                            title = "Merci pour cette édition",
                            body = "L'aftermovie est en ligne, et les photos suivent dans la semaine.",
                            url = "https://example.com/aftermovie",
                        ),
                        announcement(
                            id = "programme-2026",
                            dateText = "02.06.2026",
                            title = "Le programme complet est en ligne",
                            body = "Concerts, activités nautiques, coin enfant et jeux de plage.",
                            url = null,
                        ),
                        // Title only. The card has to hold its shape without the two lines under it.
                        announcement(
                            id = "benevoles-2026",
                            dateText = "14.05.2026",
                            title = "Les inscriptions bénévoles sont ouvertes",
                            body = "",
                            url = "https://example.com/benevoles",
                        ),
                    ),
            ),
            // Between editions, and on a first launch before the association has posted anything.
            AnnouncementsUiModel(isLoading = false, items = emptyList()),
        )

    private fun announcement(
        id: String,
        dateText: String,
        title: String,
        body: String,
        url: String?,
    ) = AnnouncementUiModel(id = id, dateText = dateText, title = title, body = body, url = url)
}

@PreviewThemes
@Composable
private fun AnnouncementsScreenPreview(
    @PreviewParameter(AnnouncementsScreenStateProvider::class) state: AnnouncementsUiModel,
) {
    YadloPreview {
        AnnouncementsScreen(state = state, onBackClick = {}, onAnnouncementClick = {})
    }
}
