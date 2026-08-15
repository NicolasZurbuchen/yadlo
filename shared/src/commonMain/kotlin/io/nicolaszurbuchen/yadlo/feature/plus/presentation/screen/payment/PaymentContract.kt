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

/**
 * [hasLoaded] rather than a nullable-means-loading state, because null is a real answer here: the
 * content may carry no payment block at all. The row that opens this screen is derived from the
 * same absence and would not have been drawn — but a restored back stack can land on a screen whose
 * section a later publish removed, and a page that spins forever is a worse way to say so.
 */
data class PaymentState(
    val payment: Payment? = null,
    val hasLoaded: Boolean = false,
)
