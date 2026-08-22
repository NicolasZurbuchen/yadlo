package io.nicolaszurbuchen.yadlo.feature.home.domain.model

import kotlin.time.Instant

/**
 * Where *now* sits against the hours the site is actually open, during the festival itself.
 *
 * **This is not a narrower Phase, it is the thing Phase deliberately is not.** `Phase.LIVE` is wide
 * on purpose — it starts at midnight on the opening Friday because that is where the visitor's head
 * is, whatever time the gates open. That width is right for deciding which tab the app opens on and
 * wrong for deciding what Accueil should say, because for **48 of LIVE's 83 hours** in the 2026
 * edition the site is shut: sixteen before the gates on Friday, ten and nine in the two overnight
 * gaps, thirteen between Sunday's close and the handover to ENDED. A screen that reads "live" for
 * all of it tells someone standing outside a closed beach at 09:00 nothing they can use.
 *
 * The windows come from [FestivalDay], whose start and end are exactly "the hours the site is open"
 * — see CONTEXT.md. Nothing here infers them from the programme: a Slot may fall outside its day's
 * window, and the yoga running from 10:00 on a day the site opens at 12:00 does not mean the site
 * is open at 10:00.
 *
 * [Finished] carries no instant because there is nothing left to name. It is the short stretch
 * between the last day closing and `Phase.ENDED` taking over the next morning, which is the whole
 * reason it exists: without it the app would go from a running festival to a thank-you in one step,
 * at whatever hour the last band stopped.
 */
sealed class SiteMoment {
    /** The first day has not opened yet — the app is live, the beach is not. */
    data class BeforeFirstDay(
        val opensAt: Instant,
    ) : SiteMoment()

    /** A FestivalDay's window is running. [closesAt] is often past midnight. */
    data class Open(
        val closesAt: Instant,
    ) : SiteMoment()

    /** Shut between two days, and coming back. */
    data class Closed(
        val reopensAt: Instant,
    ) : SiteMoment()

    /** Shut for this Edition. */
    data object Finished : SiteMoment()
}
