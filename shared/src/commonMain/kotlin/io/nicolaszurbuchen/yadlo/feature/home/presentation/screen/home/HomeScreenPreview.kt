package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloFigureUiModel
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.SocialLinkUiModel
import io.nicolaszurbuchen.yadlo.core.content.presentation.uimodel.socialIconFor
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.uimodel.QuickAccessEntryUiModel
import io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel.AnnouncementUiModel
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
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
import yadlo.shared.generated.resources.home_live_before_body
import yadlo.shared.generated.resources.home_live_before_kicker
import yadlo.shared.generated.resources.home_live_before_title
import yadlo.shared.generated.resources.home_live_closed_body
import yadlo.shared.generated.resources.home_live_closed_kicker
import yadlo.shared.generated.resources.home_live_closed_title
import yadlo.shared.generated.resources.home_live_open_body
import yadlo.shared.generated.resources.home_live_open_kicker
import yadlo.shared.generated.resources.home_live_open_title
import yadlo.shared.generated.resources.home_live_over_body
import yadlo.shared.generated.resources.home_live_over_kicker
import yadlo.shared.generated.resources.home_live_over_title
import yadlo.shared.generated.resources.home_quick_access_announced
import yadlo.shared.generated.resources.home_quick_access_approaching
import yadlo.shared.generated.resources.home_quick_access_ended
import yadlo.shared.generated.resources.home_quick_access_off_season
import yadlo.shared.generated.resources.home_thank_you_body
import yadlo.shared.generated.resources.home_thank_you_title
import yadlo.shared.generated.resources.img_atmosphere
import yadlo.shared.generated.resources.img_concert
import yadlo.shared.generated.resources.img_festival
import yadlo.shared.generated.resources.img_reception
import yadlo.shared.generated.resources.img_see_you_soon

/**
 * All five phases, because the phase *is* the screen — previewing one of them previews a fifth of
 * Accueil. In order, so scrolling the preview pane walks the year.
 *
 * The models are written out rather than mapped from a HomeState: a preview may not import the
 * domain layer, which is where HomeContent lives.
 */
private class HomeScreenStateProvider : PreviewParameterProvider<HomeUiModel> {
    override val values =
        sequenceOf(
            // Before the first bundle reaches the screen. Short-lived in practice, since the
            // splash holds until the content is ready — but it is a state the screen can render.
            HomeUiModel(isLoading = true, blocks = emptyList()),
            // OFF_SEASON — dates published, programme not yet. The widest quick access of the year,
            // because November is when there is time to read any of it.
            stack(
                countdown("J-239"),
                announcements(),
                quickAccess(Res.string.home_quick_access_off_season, offSeasonTiles()),
                social(),
            ),
            // ANNOUNCED — the programme lands and the hero points at it. One tile under it, not a
            // grid: the hero is the screen's job in this phase and nothing may compete with it.
            stack(
                countdown("J-19"),
                announcedHero(),
                announcements(),
                quickAccess(Res.string.home_quick_access_announced, announcedTiles()),
                social(),
            ),
            // APPROACHING — the only phase with something to do, and the only one with no networks.
            // Quick access sits *over* the annonces here: at J-3 it is the errand, not the sidelines.
            stack(
                countdown("J-3"),
                approachingHero(),
                quickAccess(Res.string.home_quick_access_approaching, approachingTiles()),
                announcements(),
            ),
            // LIVE, all four of it. The site is shut for roughly 48 of LIVE's 83 hours, so the
            // hero is what this tab mostly is during the festival — and the Sunday-night one is
            // the half-step that stops the weekend ending on a cliff.
            stack(
                liveHero(LIVE_BEFORE_KICKER, LIVE_BEFORE_TITLE, LIVE_BEFORE_BODY, "16:00", Res.drawable.img_reception),
                announcements(),
                social(),
            ),
            stack(
                liveHero(LIVE_OPEN_KICKER, LIVE_OPEN_TITLE, LIVE_OPEN_BODY, "02:00", Res.drawable.img_atmosphere),
                announcements(),
                social(),
            ),
            stack(
                liveHero(LIVE_CLOSED_KICKER, LIVE_CLOSED_TITLE, LIVE_CLOSED_BODY, "12:00", Res.drawable.img_reception),
                announcements(),
                social(),
            ),
            stack(liveGoodbye(), announcements(), social()),
            // ENDED — merci, the closing figures, and the way out.
            stack(
                thankYou(),
                figures(),
                announcements(),
                quickAccess(Res.string.home_quick_access_ended, endedTiles()),
                social(),
            ),
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
            image = Res.drawable.img_festival,
        )

    private fun approachingHero() =
        HomeBlockUiModel.Hero(
            kicker = UiText.Resource(Res.string.home_hero_approaching_kicker),
            title = UiText.Resource(Res.string.home_hero_approaching_title),
            body = UiText.Resource(Res.string.home_hero_approaching_body),
            image = Res.drawable.img_concert,
        )

    private fun thankYou() =
        HomeBlockUiModel.ThankYou(
            title = UiText.Resource(Res.string.home_thank_you_title),
            body = UiText.Resource(Res.string.home_thank_you_body),
            image = Res.drawable.img_see_you_soon,
        )

    private fun figures() =
        HomeBlockUiModel.Figures(
            title = UiText.Resource(Res.string.home_figures_title),
            items =
                listOf(
                    YadloFigureUiModel("visiteurs", "6000", "visiteurs"),
                    YadloFigureUiModel("heures-musique", "45", "heures de concerts"),
                    YadloFigureUiModel("litres-biere", "3200", "litres de bière"),
                    YadloFigureUiModel("benevoles", "160", "bénévoles"),
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
            hasMore = true,
        )

    private fun liveHero(
        kicker: StringResource,
        title: StringResource,
        body: StringResource,
        at: String,
        image: DrawableResource,
    ) = HomeBlockUiModel.Hero(
        kicker = UiText.Resource(kicker),
        title = UiText.Resource(title),
        body = UiText.Resource(body, listOf(at)),
        image = image,
    )

    /** The one hero with nowhere to send anyone, so the one drawn without a chevron. */
    private fun liveGoodbye() =
        HomeBlockUiModel.Hero(
            kicker = UiText.Resource(Res.string.home_live_over_kicker),
            title = UiText.Resource(Res.string.home_live_over_title),
            body = UiText.Resource(Res.string.home_live_over_body),
            image = Res.drawable.img_see_you_soon,
            opensProgramme = false,
        )

    private fun quickAccess(
        title: StringResource,
        items: List<QuickAccessItemUiModel>,
    ) = HomeBlockUiModel.QuickAccess(title = UiText.Resource(title), items = items)

    /** Three rows, and the only stack where one that leaves the app sits beside two that do not. */
    private fun offSeasonTiles() =
        listOf(
            QuickAccessItemUiModel(QuickAccessEntryUiModel.CONTACT, url = null),
            QuickAccessItemUiModel(QuickAccessEntryUiModel.NEWSLETTER, url = NEWSLETTER_URL),
            QuickAccessItemUiModel(QuickAccessEntryUiModel.STORY, url = null),
        )

    private fun announcedTiles() = listOf(QuickAccessItemUiModel(QuickAccessEntryUiModel.VOLUNTEERING, url = null))

    private fun approachingTiles() =
        listOf(
            QuickAccessItemUiModel(QuickAccessEntryUiModel.PAYMENT, url = null),
            QuickAccessItemUiModel(QuickAccessEntryUiModel.ACCESS, url = null),
        )

    private fun endedTiles() = listOf(QuickAccessItemUiModel(QuickAccessEntryUiModel.NEWSLETTER, url = NEWSLETTER_URL))

    private fun social() =
        HomeBlockUiModel.Social(
            items =
                listOf(
                    SocialLinkUiModel("instagram", UiText.Raw("Instagram"), socialIconFor("instagram"), "https://example.com/i"),
                    SocialLinkUiModel("facebook", UiText.Raw("Facebook"), socialIconFor("facebook"), "https://example.com/f"),
                    SocialLinkUiModel("youtube", UiText.Raw("YouTube"), socialIconFor("youtube"), "https://example.com/y"),
                    SocialLinkUiModel("tiktok", UiText.Raw("TikTok"), socialIconFor("tiktok"), "https://example.com/t"),
                ),
        )

    private companion object {
        val LIVE_BEFORE_KICKER = Res.string.home_live_before_kicker
        val LIVE_BEFORE_TITLE = Res.string.home_live_before_title
        val LIVE_BEFORE_BODY = Res.string.home_live_before_body
        val LIVE_OPEN_KICKER = Res.string.home_live_open_kicker
        val LIVE_OPEN_TITLE = Res.string.home_live_open_title
        val LIVE_OPEN_BODY = Res.string.home_live_open_body
        val LIVE_CLOSED_KICKER = Res.string.home_live_closed_kicker
        val LIVE_CLOSED_TITLE = Res.string.home_live_closed_title
        val LIVE_CLOSED_BODY = Res.string.home_live_closed_body

        const val NEWSLETTER_URL = "https://example.com/newsletter"
    }
}

@PreviewThemes
@Composable
private fun HomeScreenPreview(
    @PreviewParameter(HomeScreenStateProvider::class) state: HomeUiModel,
) {
    YadloPreview {
        HomeScreen(
            state = state,
            onSearchClick = {},
            onHeroClick = {},
            onAnnouncementClick = {},
            onSeeAllAnnouncementsClick = {},
            onSocialClick = {},
            onQuickAccessClick = {},
        )
    }
}
