package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

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
import yadlo.shared.generated.resources.home_announcements_title
import yadlo.shared.generated.resources.home_figures_caveat
import yadlo.shared.generated.resources.home_figures_title
import yadlo.shared.generated.resources.home_hero_announced_body
import yadlo.shared.generated.resources.home_hero_announced_kicker
import yadlo.shared.generated.resources.home_hero_announced_title
import yadlo.shared.generated.resources.home_hero_approaching_body
import yadlo.shared.generated.resources.home_hero_approaching_kicker
import yadlo.shared.generated.resources.home_hero_approaching_title
import yadlo.shared.generated.resources.home_thank_you_body
import yadlo.shared.generated.resources.home_thank_you_title

/**
 * All five phases, because the phase *is* the screen — previewing one of them previews a fifth of
 * Accueil. In order, so scrolling the preview pane walks the year.
 *
 * The models are written out rather than mapped from a HomeState: a preview may not import the
 * domain layer, which is where HomeContent lives.
 */
private class HomeStackProvider : PreviewParameterProvider<HomeUiModel> {
    override val values =
        sequenceOf(
            // OFF_SEASON — dates published, programme not yet.
            stack(countdown("J-239"), announcements(), social()),
            // ANNOUNCED — the programme lands and the hero points at it.
            stack(countdown("J-19"), announcedHero(), announcements(), social()),
            // APPROACHING — the only phase with something to do, and the only one with no networks.
            stack(countdown("J-3"), approachingHero(), announcements()),
            // LIVE — thin on purpose: during the festival the app opens on Programme.
            stack(announcements(), social()),
            // ENDED — merci, the closing figures, and the way out.
            stack(thankYou(), figures(), announcements(), social()),
        )

    private fun stack(vararg blocks: HomeBlockUiModel) = HomeUiModel(isLoading = false, blocks = blocks.toList())

    private fun countdown(daysText: String) =
        HomeBlockUiModel.Countdown(
            daysText = UiText.Raw(daysText),
            subtitle = "Yadlo 2026 · Plage de Préverenges",
        )

    private fun announcedHero() =
        HomeBlockUiModel.Hero(
            kicker = UiText.Resource(Res.string.home_hero_announced_kicker),
            title = UiText.Resource(Res.string.home_hero_announced_title, listOf("2026")),
            body = UiText.Resource(Res.string.home_hero_announced_body, listOf("13", "17", "3")),
        )

    private fun approachingHero() =
        HomeBlockUiModel.Hero(
            kicker = UiText.Resource(Res.string.home_hero_approaching_kicker),
            title = UiText.Resource(Res.string.home_hero_approaching_title),
            body = UiText.Resource(Res.string.home_hero_approaching_body),
        )

    private fun thankYou() =
        HomeBlockUiModel.ThankYou(
            title = UiText.Resource(Res.string.home_thank_you_title),
            body = UiText.Resource(Res.string.home_thank_you_body),
        )

    private fun figures() =
        HomeBlockUiModel.Figures(
            title = UiText.Resource(Res.string.home_figures_title),
            items =
                listOf(
                    FigureUiModel("visiteurs", "6000", "visiteurs"),
                    FigureUiModel("heures-musique", "45", "heures de DJ sets et concerts"),
                    FigureUiModel("litres-biere", "3200", "litres de bière"),
                    FigureUiModel("benevoles", "160", "bénévoles"),
                ),
            // The published figures are a past edition's, so the caveat is the normal case here
            // rather than the exceptional one — previewing it without would flatter the screen.
            caveat = UiText.Resource(Res.string.home_figures_caveat),
        )

    private fun announcements() =
        HomeBlockUiModel.Announcements(
            title = UiText.Resource(Res.string.home_announcements_title),
            items =
                listOf(
                    AnnouncementUiModel(
                        id = "aftermovie-2026",
                        dateText = "01.08.2026",
                        title = "Merci pour cette édition",
                        body = "L'aftermovie est en ligne.",
                        url = "https://example.com/aftermovie",
                    ),
                    AnnouncementUiModel(
                        id = "programme-2026",
                        dateText = "02.06.2026",
                        title = "Le programme complet est en ligne",
                        body = "Concerts, activités nautiques, coin enfant et jeux.",
                        url = null,
                    ),
                ),
        )

    private fun social() =
        HomeBlockUiModel.Social(
            items =
                listOf(
                    SocialUiModel("instagram", "Instagram", "https://example.com/i"),
                    SocialUiModel("facebook", "Facebook", "https://example.com/f"),
                    SocialUiModel("youtube", "YouTube", "https://example.com/y"),
                    SocialUiModel("tiktok", "TikTok", "https://example.com/t"),
                ),
        )
}

/**
 * The app paints the ground under this screen from the Scaffold, so a preview without it shows the
 * blocks floating on whatever the tooling happens to use — which is not what anyone will see.
 */
@Composable
private fun HomePreviewSurface(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
        content()
    }
}

@Preview
@Composable
private fun HomeScreenLightPreview(
    @PreviewParameter(HomeStackProvider::class) state: HomeUiModel,
) {
    YadloTheme(darkTheme = false) {
        HomePreviewSurface {
            HomeScreen(state = state, onHeroClick = {}, onAnnouncementClick = {}, onSocialClick = {})
        }
    }
}

@Preview
@Composable
private fun HomeScreenDarkPreview(
    @PreviewParameter(HomeStackProvider::class) state: HomeUiModel,
) {
    YadloTheme(darkTheme = true) {
        HomePreviewSurface {
            HomeScreen(state = state, onHeroClick = {}, onAnnouncementClick = {}, onSocialClick = {})
        }
    }
}
