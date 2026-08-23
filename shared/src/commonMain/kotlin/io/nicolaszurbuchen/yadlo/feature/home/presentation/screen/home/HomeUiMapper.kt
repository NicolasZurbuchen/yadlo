package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

import io.nicolaszurbuchen.yadlo.app.design.uimodel.YadloFigureUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.SocialLinkUiModel
import io.nicolaszurbuchen.yadlo.common.content.presentation.uimodel.socialIconFor
import io.nicolaszurbuchen.yadlo.common.time.FESTIVAL_TIME_ZONE
import io.nicolaszurbuchen.yadlo.feature.home.presentation.uimodel.AnnouncementUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsShortDate
import io.nicolaszurbuchen.yadlo.infra.ui.formatAsTimeOfDay
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
import kotlin.time.Duration.Companion.hours

/**
 * The block stack, decided per Phase, following the published Accueil prototype.
 *
 * Blocks the prototype shows that are still absent have nowhere to send anyone yet: revivre
 * l'édition and les réservations. Both wait on a screen that does not exist — there is no archive
 * of past editions, and nothing in the published content is a booking. S'impliquer, la newsletter,
 * l'histoire and préparer sa venue have arrived, as the tiles of [HomeBlockUiModel.QuickAccess],
 * and the quiet hours as [liveHero].
 *
 * **La recherche has arrived too**, as [HomeBlockUiModel.Search] — a button dressed as a field, and
 * the first block in each of the three phases DECISIONS.md gives it. The three tabs that are not
 * Accueil reach the same screen through the magnifier in the shell's toolbar; here it is a block,
 * because this is the screen with room to teach that the app has a search at all.
 *
 * The phases they belong to are where they go; the ordering below is not a suggestion, it is the
 * prototype's.
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
                // A crowd in front of a stage, three days out: the thing the Plan is being built
                // for, rather than the site it happens on.
                image = Res.drawable.img_concert,
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
                // The festival at large, because this hero announces the whole of it and not one
                // evening of it. Its own file rather than the fallback photograph it was copied
                // from — see the note in HomeScreenPreview.
                image = Res.drawable.img_festival,
            )
        }

    // **What Accueil says during the festival, which is mostly "not right now".** `Phase.LIVE` is
    // wide on purpose — it starts at midnight on the opening Friday because that is where the
    // visitor's head is — and for roughly 48 of its 83 hours the site itself is shut. Without this
    // the tab spends most of the weekend showing a social row and whatever was posted in the last
    // day, which reads as broken rather than as closed.
    //
    // Every variant but the last opens the Programme, because in each of them there is still a
    // programme to look at: what is on tonight, what is on tomorrow.
    val liveHero =
        when (siteMoment) {
            null -> {
                null
            }

            is SiteMomentUiModel.BeforeFirstDay -> {
                HomeBlockUiModel.Hero(
                    kicker = UiText.Resource(Res.string.home_live_before_kicker),
                    title = UiText.Resource(Res.string.home_live_before_title),
                    body =
                        UiText.Resource(
                            Res.string.home_live_before_body,
                            listOf(siteMoment.opensAt.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)),
                        ),
                    // The gate, on both the states that are waiting outside it.
                    image = Res.drawable.img_reception,
                )
            }

            // The closing time is the useful fact here and it is nowhere else on this tab — "how
            // long have I got" is the question someone on the beach actually has. The alternative
            // considered was sending the reader to the Programme and saying nothing, which is a
            // signpost to a tab the app already opened on.
            is SiteMomentUiModel.Open -> {
                HomeBlockUiModel.Hero(
                    kicker = UiText.Resource(Res.string.home_live_open_kicker),
                    title = UiText.Resource(Res.string.home_live_open_title),
                    body =
                        UiText.Resource(
                            Res.string.home_live_open_body,
                            listOf(siteMoment.closesAt.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)),
                        ),
                    image = Res.drawable.img_atmosphere,
                )
            }

            // Deliberately not "c'est fini pour ce soir": this same state is what someone reads at
            // 02:30 and again at 10:00 the next morning, and only one of those is an evening.
            is SiteMomentUiModel.Closed -> {
                HomeBlockUiModel.Hero(
                    kicker = UiText.Resource(Res.string.home_live_closed_kicker),
                    title = UiText.Resource(Res.string.home_live_closed_title),
                    body =
                        UiText.Resource(
                            Res.string.home_live_closed_body,
                            listOf(siteMoment.reopensAt.formatAsTimeOfDay(FESTIVAL_TIME_ZONE)),
                        ),
                    image = Res.drawable.img_reception,
                )
            }

            // The half-step that stops the weekend ending on a cliff. The site shuts at 22:00 on
            // the Sunday and ENDED does not take over until 11:00 the next morning, so without this
            // the last thirteen hours would still read as a running festival. It says goodbye
            // lightly; the real thank-you, with the photograph, is what the morning brings.
            SiteMomentUiModel.Finished -> {
                HomeBlockUiModel.Hero(
                    kicker = UiText.Resource(Res.string.home_live_over_kicker),
                    title = UiText.Resource(Res.string.home_live_over_title),
                    body = UiText.Resource(Res.string.home_live_over_body),
                    // The same farewell the ENDED thank-you carries, thirteen hours earlier and
                    // in a quieter register. Shared on purpose: nobody sees the two at once, and
                    // one picture across both reads as one long goodbye rather than two screens.
                    image = Res.drawable.img_see_you_soon,
                    opensProgramme = false,
                )
            }
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

    // Each promoted tile is gated on the section behind it, exactly as the matching Plus row is, so
    // Accueil can never offer a screen with nothing on it. The two entries that leave the app carry
    // their address; the three that stay carry null, because the navigator already knows the key.
    val volunteering =
        QuickAccessItemUiModel(entry = QuickAccessEntryUiModel.VOLUNTEERING, url = null)
            .takeIf { loaded.hasVolunteering }
    val contact =
        QuickAccessItemUiModel(entry = QuickAccessEntryUiModel.CONTACT, url = null)
            .takeIf { loaded.hasContact }
    val story =
        QuickAccessItemUiModel(entry = QuickAccessEntryUiModel.STORY, url = null)
            .takeIf { loaded.hasStory }
    val payment =
        QuickAccessItemUiModel(entry = QuickAccessEntryUiModel.PAYMENT, url = null)
            .takeIf { loaded.hasPayment }
    val access =
        QuickAccessItemUiModel(entry = QuickAccessEntryUiModel.ACCESS, url = null)
            .takeIf { loaded.hasTransport }
    val newsletter =
        loaded.newsletterUrl?.let { QuickAccessItemUiModel(entry = QuickAccessEntryUiModel.NEWSLETTER, url = it) }

    // **Which tiles a Phase gets, and what the block calls them.** This is the block's whole
    // argument, so it is one readable table rather than five conditions scattered down the file.
    //
    // The counts are uneven on purpose — one in ANNOUNCED, three in OFF_SEASON — because a tile
    // earns its place by being actionable *now*, and filling every phase to the same width would
    // mean promoting things nobody is ready to act on. ANNOUNCED gets exactly one because the hero
    // is doing the work in that phase and a grid under it would compete with the one thing the
    // screen is for.
    //
    // **LIVE gets nothing, and that is a decision rather than a gap** — DECISIONS.md § Accueil,
    // block by block turned down the plan du site and the stands here by name, on the grounds that
    // both already live in Plus › Sur place, and the app is meant to open on Programme during the
    // festival anyway. A block promoting Plus screens onto a tab nobody is looking at that weekend
    // is the duplication rule with extra steps.
    val quickAccess =
        when (phase) {
            // *Nous écrire* rather than *Devenir bénévole*, which is the difference between the two
            // long phases: between editions there is nothing to staff yet, and the useful offer is
            // a way to reach the association during the months it can actually answer.
            PhaseUiModel.OFF_SEASON -> {
                Res.string.home_quick_access_off_season to listOfNotNull(contact, newsletter, story)
            }

            // And now there is: the programme exists, so the edition is a thing that has to be
            // staffed, and this is the phase the association is actually recruiting in.
            PhaseUiModel.ANNOUNCED -> {
                Res.string.home_quick_access_announced to listOfNotNull(volunteering)
            }

            // Paiement first of the two: it is the only fact in the app that is actionable
            // exclusively *before leaving the house*, which is when someone would otherwise stop at
            // a cash machine they did not need.
            PhaseUiModel.APPROACHING -> {
                Res.string.home_quick_access_approaching to listOfNotNull(payment, access)
            }

            PhaseUiModel.LIVE -> {
                null
            }

            // The Monday after is the one moment someone has just decided they are coming back, and
            // the newsletter is the only thing the app can offer them to act on it.
            PhaseUiModel.ENDED -> {
                Res.string.home_quick_access_ended to listOfNotNull(newsletter)
            }
        }?.let { (title, items) ->
            items
                .takeIf { it.isNotEmpty() }
                ?.let { HomeBlockUiModel.QuickAccess(title = UiText.Resource(title), items = it) }
        }

    val blocks =
        when (phase) {
            // Quick access sits under the annonces in the two long phases and over them in
            // APPROACHING, which is the prototype's ordering and follows from what each is for:
            // in June the annonces are the news and these are the sidelines, while at J-3 they are
            // the errand and the annonces are the news about it.
            PhaseUiModel.OFF_SEASON -> {
                listOfNotNull(countdown, announcements, quickAccess, social)
            }

            PhaseUiModel.ANNOUNCED -> {
                listOfNotNull(HomeBlockUiModel.Search, countdown, hero, announcements, quickAccess, social)
            }

            // No networks in this one phase, and that is the prototype's call rather than an
            // omission: it is the only phase with something to do, and it ends on the annonces
            // instead of offering a way off the app three days before the gates open.
            PhaseUiModel.APPROACHING -> {
                listOfNotNull(HomeBlockUiModel.Search, countdown, hero, quickAccess, announcements)
            }

            // The hero leads, because during the festival the first question is whether the site
            // is open at all, and the annonces underneath are the answer to a different one.
            PhaseUiModel.LIVE -> {
                listOfNotNull(HomeBlockUiModel.Search, liveHero, announcements, social)
            }

            PhaseUiModel.ENDED -> {
                listOfNotNull(thankYou, figures, announcements, quickAccess, social)
            }
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
