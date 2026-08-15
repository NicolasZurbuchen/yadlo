package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *Nous écrire* — an aiguillage, not a form.
 *
 * Nothing on this screen is posted anywhere. Each choice opens something that already exists: the
 * association's own recruitment site, or the visitor's own mail app. That keeps their pipeline
 * receiving its applications and keeps this app from becoming a data processor.
 */
data class ContactUiModel(
    val isLoading: Boolean,
    val volunteering: VolunteeringUiModel?,
    val emails: List<ContactEmailUiModel>,
    val address: String?,
    val emptyMessage: UiText?,
)

/** [perks] are what the association offers in return, in its own words. */
data class VolunteeringUiModel(
    val name: String,
    val body: String,
    val perks: List<String>,
    val signupUrl: String?,
    val email: String?,
)

/** [label] is what the address is for — "Programmation musicale", "Food trucks". */
data class ContactEmailUiModel(
    val id: String,
    val label: String,
    val address: String,
)
