package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.partners

import io.nicolaszurbuchen.yadlo.core.content.domain.model.PartnerTier

sealed interface PartnersIntent {
    /**
     * [url] is null for the five partners that have no site. The tap is still an intent rather than
     * being swallowed by the row, because *a partner without a website must say so* — a tap that
     * does nothing reads as a bug, and here it would be the common case rather than the edge one.
     */
    data class PartnerClicked(
        val url: String?,
    ) : PartnersIntent
}

sealed interface PartnersLabel {
    data class OpenUrl(
        val url: String,
    ) : PartnersLabel
}

sealed interface PartnersAction {
    data object ObserveTiers : PartnersAction
}

sealed interface PartnersMessage {
    data class TiersUpdated(
        val tiers: List<PartnerTier>,
    ) : PartnersMessage

    data object NoWebsiteTapped : PartnersMessage
}

/**
 * [noWebsiteTaps] counts taps on a partner with no site rather than publishing a Label for them.
 *
 * A Label would be the obvious shape and cannot be tested for the thing that matters — that the
 * *second* tap says so again. Counting makes it state: the screen keys its snackbar on the number,
 * so two taps produce two messages and re-entering the screen produces none.
 */
data class PartnersState(
    val tiers: List<PartnerTier>? = null,
    val noWebsiteTaps: Int = 0,
)
