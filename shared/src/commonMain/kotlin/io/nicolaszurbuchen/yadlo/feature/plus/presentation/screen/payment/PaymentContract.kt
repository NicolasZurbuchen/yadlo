package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import io.nicolaszurbuchen.yadlo.common.content.domain.model.Payment

sealed interface PaymentIntent {
    data class LinkClicked(
        val url: String,
    ) : PaymentIntent
}

sealed interface PaymentLabel {
    data class OpenUrl(
        val url: String,
    ) : PaymentLabel
}

sealed interface PaymentAction {
    data object ObservePayment : PaymentAction
}

sealed interface PaymentMessage {
    data class PaymentUpdated(
        val payment: Payment?,
    ) : PaymentMessage
}

/** A null [payment] is the bundle not having landed yet. There is always a payment block. */
data class PaymentState(
    val payment: Payment? = null,
)
