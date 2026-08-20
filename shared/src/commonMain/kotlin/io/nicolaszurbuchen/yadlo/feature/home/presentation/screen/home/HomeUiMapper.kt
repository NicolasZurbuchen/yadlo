package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloFigureUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SocialLinkUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.socialIconFor
import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel.AnnouncementUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsShortDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_announcements_title
import yadlo.shared.generated.resources.home_countdown_days_remaining
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
import yadlo.shared.generated.resources.img_see_you_soon
import kotlin.time.Duration.Companion.hours

/**
 * The block stack, decided per Phase, following the published Accueil prototype.
 *
 * Blocks the prototype shows that are absent here have nowhere to send anyone yet: recherche,
 * revivre l'édition, s'impliquer, la newsletter, l'histoire, préparer sa venue, les réservations,
 * and the LIVE quiet-hours block. Each waits on either the unmodelled Plus sections of
 * `festival.json`, a screen that does not exist, or Slot live state. The phases they belong to are
 * where they go; the ordering below is not a suggestion, it is the prototype's.
 *
 * Every block is built before one `when` picks the stack, rather than each being built inside the
 * branch that wants it. They are small immutable values, and a UiMapper is required to be a single
 * top-level extension function — helpers would have to be local, which Konsist reads as extra
 * functions in the file.
 */
fun HomeState.toUiModel(): HomeUiModel {
    val loaded = content ?: return HomeUiModel(isLoading = true, blocks = emptyList())

    // Counted in calendar days in the festival's own zone, never by dividing a Duration: "J-3"
    // means three sleeps away, and it has to say the same thing at 23:00 as it did at 09:00.
    val today = now.toLocalDateTime(FESTIVAL_TIME_ZONE).date
    val opensOn = loaded.days.minOfOrNull { it.start }?.toLocalDateTime(FESTIVAL_TIME_ZONE)?.date
    val daysRemaining = opensOn?.let { today.daysUntil(it) }
    val countdown =
        // Never counts down to a date already past: between editions the file on hand is usually
        // the one that just finished, and a countdown running backwards is worse than none.
        if (daysRemaining == null || daysRemaining <= 0) {
            null
        } else {
            HomeBlockUiModel.Countdown(
                daysText = UiText.Resource(Res.string.home_countdown_days_remaining, listOf(daysRemaining.toString())),
                // The dates themselves are in the app bar, on screen at the same time — repeating
                // them here would be the duplication rule with a two-inch gap.
                subtitle = "${loaded.editionName} · ${loaded.venueName}",
            )
        }

    val hero =
        if (phase == PhaseUiModel.APPROACHING) {
            HomeBlockUiModel.Hero(
                kicker = UiText.Resource(Res.string.home_hero_approaching_kicker),
                title = UiText.Resource(Res.string.home_hero_approaching_title),
                body = UiText.Resource(Res.string.home_hero_approaching_body),
            )
        } else {
            HomeBlockUiModel.Hero(
                kicker = UiText.Resource(Res.string.home_hero_announced_kicker),
                title = UiText.Resource(Res.string.home_hero_announced_title, listOf(loaded.editionYear.toString())),
                body =
                    UiText.Resource(
                        Res.string.home_hero_announced_body,
                        listOf(
                            loaded.artistCount.toString(),
                            loaded.activityCount.toString(),
                            loaded.days.size.toString(),
                        ),
                    ),
            )
        }

    val thankYou =
        HomeBlockUiModel.ThankYou(
            title = UiText.Resource(Res.string.home_thank_you_title),
            body = UiText.Resource(Res.string.home_thank_you_body),
            // Bundled rather than fetched: ENDED is the Phase the app spends its offline months in,
            // and a thank-you that fails to load is worse than no photograph at all.
            image = Res.drawable.img_see_you_soon,
        )

    val figures =
        if (loaded.figures.isEmpty()) {
            null
        } else {
            HomeBlockUiModel.Figures(
                title = UiText.Resource(Res.string.home_figures_title),
                items = loaded.figures.map { YadloFigureUiModel(id = it.id, value = it.value, label = it.label) },
                // Provenance earning its keep. The association has published closing figures once,
                // so a block that waits for fresh ones would be empty for most of its life; showing
                // them and saying where they came from is the honest version of the same block.
                caveat = if (loaded.figuresAreConfirmed) null else UiText.Resource(Res.string.home_figures_caveat),
            )
        }

    // "Annonces du jour" during LIVE: on site the feed is a running commentary, and a fortnight-old
    // ticketing post pushes this morning's change of stage below the fold.
    val visibleAnnouncements =
        if (phase == PhaseUiModel.LIVE) {
            loaded.announcements.filter { now - it.publishedAt <= LIVE_ANNOUNCEMENT_WINDOW }
        } else {
            loaded.announcements
        }
    val announcements =
        if (visibleAnnouncements.isEmpty()) {
            null
        } else {
            HomeBlockUiModel.Announcements(
                title = UiText.Resource(Res.string.home_announcements_title),
                // Accueil is a summary, not the feed. The rest are one tap away on their own screen.
                items =
                    visibleAnnouncements.take(ANNOUNCEMENTS_ON_ACCUEIL).map { announcement ->
                        AnnouncementUiModel(
                            id = announcement.id,
                            dateText = announcement.publishedAt.formatAsShortDate(FESTIVAL_TIME_ZONE),
                            title = announcement.title,
                            body = announcement.body.orEmpty(),
                            url = announcement.url,
                        )
                    },
                hasMore = visibleAnnouncements.size > ANNOUNCEMENTS_ON_ACCUEIL,
            )
        }

    val social =
        if (loaded.social.isEmpty()) {
            null
        } else {
            HomeBlockUiModel.Social(
                items =
                    loaded.social.map {
                        SocialLinkUiModel(
                            id = it.id,
                            // Raw, not a resource: the association's networks are named by the
                            // content, and a brand name does not translate.
                            name = UiText.Raw(it.name),
                            icon = socialIconFor(it.id),
                            url = it.url,
                        )
                    },
            )
        }

    val blocks =
        when (phase) {
            PhaseUiModel.OFF_SEASON -> listOfNotNull(countdown, announcements, social)

            PhaseUiModel.ANNOUNCED -> listOfNotNull(countdown, hero, announcements, social)

            // No networks in this one phase, and that is the prototype's call rather than an
            // omission: it is the only phase with something to do, and it ends on the annonces
            // instead of offering a way off the app three days before the gates open.
            PhaseUiModel.APPROACHING -> listOfNotNull(countdown, hero, announcements)

            PhaseUiModel.LIVE -> listOfNotNull(announcements, social)

            PhaseUiModel.ENDED -> listOfNotNull(thankYou, figures, announcements, social)
        }

    return HomeUiModel(isLoading = false, blocks = blocks)
}

/**
 * Two, because Accueil summarises and the full list is one tap away. The prototype shows two even
 * during LIVE, when the feed is at its busiest.
 */
private const val ANNOUNCEMENTS_ON_ACCUEIL = 2

/**
 * Twenty-four hours rather than "since midnight": the festival runs past midnight, so a calendar
 * day would drop an annonce posted at 01:00 the moment the reader is most likely to open the app.
 */
private val LIVE_ANNOUNCEMENT_WINDOW = 24.hours
