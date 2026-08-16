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
 * [hasLoaded] and a nullable [offer] are two facts, not one: the bundle has not landed yet, versus
 * it landed with no campaign in it. Recruiting closes, and when it does this screen has to be able
 * to say so rather than spin.
 */
data class VolunteeringState(
    val offer: VolunteeringOffer? = null,
    val hasLoaded: Boolean = false,
)
