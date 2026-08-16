package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

/**
 * *Nous écrire* — an aiguillage, not a form.
 *
 * Nothing on this screen is posted anywhere. Every choice opens the visitor's own mail app against
 * an address the association already publishes, which keeps their inboxes receiving their own mail
 * and keeps this app from becoming a data processor.
 */
data class ContactUiModel(
    val isLoading: Boolean,
    val emails: List<ContactEmailUiModel>,
    val address: String?,
)

/**
 * [label] is what the address is for — "Programmation musicale", "Food trucks".
 *
 * [responsible] is who is behind it. Kept as a field of its own rather than folded into the label,
 * because the two answer different questions: the label is what you scan for, and the name is what
 * turns writing into a role into writing to somebody. Null on the general address, which has no one
 * in particular behind it by design.
 */
data class ContactEmailUiModel(
    val id: String,
    val label: String,
    val responsible: String?,
    val address: String,
)
