package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

/**
 * *Paiement* — the single most consequential practical fact the festival publishes, and the one its
 * own site buries deepest.
 *
 * **The answer first, then the list that supports it.** [headline] and [summary] are the whole
 * screen for most readers; [methods] is for the one holding a Maestro card who wants to be sure.
 * The earlier version opened with two sections — what works, then what does not — which made
 * everyone assemble the sentence out of a list before they could put the phone away.
 *
 * [methods] keeps the refusal *in* the list rather than in a section of its own. A single column
 * with one ✕ in it is where the odd one out is findable at a glance; two sections made the reader
 * read both to be sure which was which.
 */
data class PaymentUiModel(
    val isLoading: Boolean,
    val headline: String?,
    val summary: String?,
    val methods: List<PaymentMethodUiModel>,
    val notes: List<PaymentNoteUiModel>,
)

data class PaymentMethodUiModel(
    val id: String,
    val name: String,
    val accepted: Boolean,
)

/**
 * A heading, a paragraph, and the links that belong under *that* heading rather than to the page.
 *
 * [title] is null for a note the published content has not given a heading, which is what content
 * older than the build that reads it looks like. The paragraph then stands on its own.
 */
data class PaymentNoteUiModel(
    val id: String,
    val title: String?,
    val body: String,
    val links: List<PaymentLinkUiModel>,
)

data class PaymentLinkUiModel(
    val id: String,
    val label: String,
    val sublabel: String?,
    val url: String,
)
