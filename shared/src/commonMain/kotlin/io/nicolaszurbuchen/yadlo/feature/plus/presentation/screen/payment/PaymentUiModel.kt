package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *Paiement* — the single most consequential practical fact the festival publishes, and the one its
 * own site buries deepest.
 *
 * **[refused] is not the leftovers of [accepted].** A list of what works answers "have I got one of
 * these"; the refusal answers "do I need to stop at a cash machine", which is the only question
 * that has to be settled before leaving the house. They are two sections, and the refusal is not
 * hidden at the bottom of the first.
 */
data class PaymentUiModel(
    val isLoading: Boolean,
    val accepted: List<String>,
    val refused: List<String>,
    val notes: List<String>,
    val links: List<PaymentLinkUiModel>,
    val emptyMessage: UiText?,
)

data class PaymentLinkUiModel(
    val id: String,
    val label: String,
    val sublabel: String?,
    val url: String,
)
