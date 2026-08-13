package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.home_announcements_title
import yadlo.shared.generated.resources.home_countdown_days
import yadlo.shared.generated.resources.home_countdown_hours
import yadlo.shared.generated.resources.home_countdown_minutes
import yadlo.shared.generated.resources.home_countdown_seconds
import yadlo.shared.generated.resources.home_countdown_title
import yadlo.shared.generated.resources.home_figures_title
import yadlo.shared.generated.resources.home_hero_announced_action
import yadlo.shared.generated.resources.home_hero_announced_body
import yadlo.shared.generated.resources.home_hero_announced_title
import yadlo.shared.generated.resources.home_hero_approaching_action
import yadlo.shared.generated.resources.home_hero_approaching_body
import yadlo.shared.generated.resources.home_hero_approaching_title
import yadlo.shared.generated.resources.home_thank_you_body
import yadlo.shared.generated.resources.home_thank_you_title
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

/**
 * The block stack, decided per Phase, exactly as DECISIONS.md enumerates it.
 *
 * Blocks that need content or screens which do not exist yet are absent rather than stubbed:
 * recherche, s'impliquer, newsletter, l'histoire, réseaux sociaux, archives, préparer sa venue,
 * réservations, the Instagram link-out and the LIVE quiet-hours block. Every one of them waits on
 * either the unmodelled Plus sections of `festival.json`, a screen to send the visitor to, or Slot
 * live state. The phases they belong to are where they go; the ordering below is not a suggestion,
 * it is the order those documents settled on.
 *
 * Every block is built before one `when` picks the stack, rather than each being built inside the
 * branch that wants it. They are small immutable values, and a UiMapper is required to be a single
 * top-level extension function — helpers would have to be local, which Konsist reads as extra
 * functions in the file.
 */
fun HomeState.toUiModel(): HomeUiModel {
    val loaded = content ?: return HomeUiModel(isLoading = true, blocks = emptyList())

    // Never counts down to a date already past: in OFF_SEASON the edition on file is usually the
    // one that just finished, and a countdown running backwards is worse than no countdown.
    val remaining = loaded.days.minOfOrNull { it.start }?.minus(now)
    val countdown =
        if (remaining == null || remaining <= Duration.ZERO) {
            null
        } else {
            remaining.toComponents { days, hours, minutes, seconds, _ ->
                // Days are not padded: "6" is a number of sleeps, while "04" is a reading on a
                // clock, and padding the first would make it look like a fourth clock face.
                val clockCells =
                    listOf(
                        hours to Res.string.home_countdown_hours,
                        minutes to Res.string.home_countdown_minutes,
                        seconds to Res.string.home_countdown_seconds,
                    )

                HomeBlockUiModel.Countdown(
                    title = UiText.Resource(Res.string.home_countdown_title),
                    editionName = loaded.editionName,
                    cells =
                        listOf(
                            CountdownCellUiModel(
                                value = days.toString(),
                                label = UiText.Resource(Res.string.home_countdown_days),
                            ),
                        ) +
                            clockCells.map { (value, label) ->
                                CountdownCellUiModel(
                                    value = value.toString().padStart(2, '0'),
                                    label = UiText.Resource(label),
                                )
                            },
                )
            }
        }

    val hero =
        if (phase == PhaseUiModel.APPROACHING) {
            HomeBlockUiModel.Hero(
                title = UiText.Resource(Res.string.home_hero_approaching_title),
                body = UiText.Resource(Res.string.home_hero_approaching_body),
                actionLabel = UiText.Resource(Res.string.home_hero_approaching_action),
            )
        } else {
            HomeBlockUiModel.Hero(
                title = UiText.Resource(Res.string.home_hero_announced_title),
                body = UiText.Resource(Res.string.home_hero_announced_body),
                actionLabel = UiText.Resource(Res.string.home_hero_announced_action),
            )
        }

    val thankYou =
        HomeBlockUiModel.ThankYou(
            title = UiText.Resource(Res.string.home_thank_you_title),
            body = UiText.Resource(Res.string.home_thank_you_body, listOf(loaded.editionName)),
        )

    val figures =
        if (loaded.figures.isEmpty()) {
            null
        } else {
            HomeBlockUiModel.Figures(
                title = UiText.Resource(Res.string.home_figures_title),
                items = loaded.figures.map { FigureUiModel(id = it.id, value = it.value, label = it.label) },
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
                items =
                    visibleAnnouncements.map { announcement ->
                        val date = announcement.publishedAt.toLocalDateTime(FESTIVAL_TIME_ZONE).date
                        val dayOfMonth = date.day.toString().padStart(2, '0')
                        val month = date.month.number.toString().padStart(2, '0')

                        AnnouncementUiModel(
                            id = announcement.id,
                            // Numeric and Swiss-ordered, so no month name has to be translated
                            // before the app has a second language to translate it into.
                            dateText = "$dayOfMonth.$month.${date.year}",
                            title = announcement.title,
                            body = announcement.body,
                            url = announcement.url,
                        )
                    },
            )
        }

    val blocks =
        when (phase) {
            PhaseUiModel.OFF_SEASON -> listOfNotNull(countdown, announcements)
            PhaseUiModel.ANNOUNCED -> listOfNotNull(countdown, hero, announcements)
            PhaseUiModel.APPROACHING -> listOfNotNull(countdown, hero, announcements)
            PhaseUiModel.LIVE -> listOfNotNull(announcements)
            PhaseUiModel.ENDED -> listOfNotNull(thankYou, figures, announcements)
        }

    return HomeUiModel(isLoading = false, blocks = blocks)
}

/**
 * Twenty-four hours rather than "since midnight": the festival runs past midnight, so a calendar
 * day would drop an annonce posted at 01:00 the moment the reader is most likely to open the app.
 */
private val LIVE_ANNOUNCEMENT_WINDOW = 24.hours
