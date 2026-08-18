package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel.PlusEmailUiModel

/**
 * *Nous écrire* — an aiguillage, not a form.
 *
 * Nothing on this screen is posted anywhere. Every choice opens the visitor's own mail app against
 * an address the association already publishes, which keeps their inboxes receiving their own mail
 * and keeps this app from becoming a data processor.
 *
 * The addresses are [PlusEmailUiModel] rather than a type of this screen's own, because *Devenir
 * Hot'Staff* offers one of these same addresses and should not be describing it differently.
 */
data class ContactUiModel(
    val isLoading: Boolean,
    val emails: List<PlusEmailUiModel>,
    val address: String?,
)
