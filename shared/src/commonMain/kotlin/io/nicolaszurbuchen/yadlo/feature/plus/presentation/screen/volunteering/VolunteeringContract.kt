package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import io.nicolaszurbuchen.yadlo.feature.plus.domain.model.VolunteeringOffer

sealed interface VolunteeringIntent {
    data class SignupClicked(
        val url: String,
    ) : VolunteeringIntent

    data class EmailClicked(
        val address: String,
    ) : VolunteeringIntent
}

sealed interface VolunteeringLabel {
    data class OpenUrl(
        val url: String,
    ) : VolunteeringLabel
}

sealed interface VolunteeringAction {
    data object ObserveOffer : VolunteeringAction
}

sealed interface VolunteeringMessage {
    data class OfferUpdated(
        val offer: VolunteeringOffer?,
    ) : VolunteeringMessage
}

/**
 * A null [offer] is the bundle not having landed yet.
 *
 * Recruiting closing is deliberately not a state here — see DECISIONS.md § Open. The row that opens
 * this screen is derived from the same block, so an edition between campaigns loses the row rather
 * than opening a page that says the campaign is over.
 */
data class VolunteeringState(
    val offer: VolunteeringOffer? = null,
)
