package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_announcements_title
import yadlo.shared.generated.resources.home_countdown_days
import yadlo.shared.generated.resources.home_countdown_hours
import yadlo.shared.generated.resources.home_countdown_minutes
import yadlo.shared.generated.resources.home_countdown_seconds
import yadlo.shared.generated.resources.home_countdown_title
import yadlo.shared.generated.resources.home_figures_title
import yadlo.shared.generated.resources.home_hero_approaching_action
import yadlo.shared.generated.resources.home_hero_approaching_body
import yadlo.shared.generated.resources.home_hero_approaching_title
import yadlo.shared.generated.resources.home_thank_you_body
import yadlo.shared.generated.resources.home_thank_you_title

/**
 * The two stacks that differ most: the week before, and the six weeks after. Between them they
 * cover every block that exists, which is the point of previewing a screen assembled from a list.
 */
private class HomeStackProvider : PreviewParameterProvider<HomeUiModel> {
    private val announcements =
        HomeBlockUiModel.Announcements(
            title = UiText.Resource(Res.string.home_announcements_title),
            items =
                listOf(
                    AnnouncementUiModel(
                        id = "programme-2026",
                        dateText = "02.06.2026",
                        title = "Le programme complet est en ligne",
                        body = "Concerts, activités nautiques, coin enfant et jeux.",
                        url = null,
                    ),
                    AnnouncementUiModel(
                        id = "aftermovie-2026",
                        dateText = "01.08.2026",
                        title = "Merci pour cette édition",
                        body = "L'aftermovie est en ligne.",
                        url = "https://example.com/aftermovie",
                    ),
                ),
        )

    override val values =
        sequenceOf(
            HomeUiModel(
                isLoading = false,
                blocks =
                    listOf(
                        HomeBlockUiModel.Countdown(
                            title = UiText.Resource(Res.string.home_countdown_title),
                            editionName = "Yadlo 2026",
                            cells =
                                listOf(
                                    CountdownCellUiModel("6", UiText.Resource(Res.string.home_countdown_days)),
                                    CountdownCellUiModel("04", UiText.Resource(Res.string.home_countdown_hours)),
                                    CountdownCellUiModel("12", UiText.Resource(Res.string.home_countdown_minutes)),
                                    CountdownCellUiModel("30", UiText.Resource(Res.string.home_countdown_seconds)),
                                ),
                        ),
                        HomeBlockUiModel.Hero(
                            title = UiText.Resource(Res.string.home_hero_approaching_title),
                            body = UiText.Resource(Res.string.home_hero_approaching_body),
                            actionLabel = UiText.Resource(Res.string.home_hero_approaching_action),
                        ),
                        announcements,
                    ),
            ),
            HomeUiModel(
                isLoading = false,
                blocks =
                    listOf(
                        HomeBlockUiModel.ThankYou(
                            title = UiText.Resource(Res.string.home_thank_you_title),
                            body = UiText.Resource(Res.string.home_thank_you_body, listOf("Yadlo 2026")),
                        ),
                        HomeBlockUiModel.Figures(
                            title = UiText.Resource(Res.string.home_figures_title),
                            items =
                                listOf(
                                    FigureUiModel("visiteurs", "6000", "visiteurs"),
                                    FigureUiModel("heures-musique", "45", "heures de DJ sets et concerts"),
                                    FigureUiModel("litres-biere", "3200", "litres de bière"),
                                    FigureUiModel("benevoles", "160", "bénévoles"),
                                ),
                        ),
                        announcements,
                    ),
            ),
        )
}

@Preview
@Composable
private fun HomeScreenLightPreview(
    @PreviewParameter(HomeStackProvider::class) state: HomeUiModel,
) {
    YadloTheme(darkTheme = false) {
        HomeScreen(state = state, onHeroClick = {}, onAnnouncementClick = {})
    }
}

@Preview
@Composable
private fun HomeScreenDarkPreview(
    @PreviewParameter(HomeStackProvider::class) state: HomeUiModel,
) {
    YadloTheme(darkTheme = true) {
        HomeScreen(state = state, onHeroClick = {}, onAnnouncementClick = {})
    }
}
