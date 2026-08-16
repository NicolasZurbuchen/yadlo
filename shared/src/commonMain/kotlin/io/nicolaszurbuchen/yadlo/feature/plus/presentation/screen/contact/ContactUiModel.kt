package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import io.nicolaszurbuchen.yadlo.infra.ui.UiText

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
    val emptyMessage: UiText?,
)

/** [label] is what the address is for — "Programmation musicale", "Food trucks". */
data class ContactEmailUiModel(
    val id: String,
    val label: String,
    val address: String,
)
