package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.access

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Transport

sealed interface AccessIntent {
    data class LinkClicked(
        val url: String,
    ) : AccessIntent
}

sealed interface AccessLabel {
    data class OpenUrl(
        val url: String,
    ) : AccessLabel
}

sealed interface AccessAction {
    data object ObserveTransport : AccessAction
}

sealed interface AccessMessage {
    data class TransportUpdated(
        val transport: Transport?,
    ) : AccessMessage
}

/** [hasLoaded] for the reason PaymentState gives: null is a real answer, not only a waiting one. */
data class AccessState(
    val transport: Transport? = null,
    val hasLoaded: Boolean = false,
)
