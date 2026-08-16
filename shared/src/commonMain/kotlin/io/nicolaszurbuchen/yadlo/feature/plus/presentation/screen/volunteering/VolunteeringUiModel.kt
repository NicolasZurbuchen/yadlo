package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *Devenir Hot'Staff* — what is asked, what is offered, and the two ways to act on it.
 *
 * [name] comes from the content rather than from a string, because the programme is branded and the
 * association renames it more readily than it renames a tab. [emptyMessage] is what stands in when
 * recruiting has closed: a screen that says the campaign is over is worth more than a row that
 * silently disappeared while somebody was looking for it.
 */
data class VolunteeringUiModel(
    val isLoading: Boolean,
    val name: String?,
    val body: String?,
    val perks: List<String>,
    val signupUrl: String?,
    val email: String?,
    val emptyMessage: UiText?,
)
