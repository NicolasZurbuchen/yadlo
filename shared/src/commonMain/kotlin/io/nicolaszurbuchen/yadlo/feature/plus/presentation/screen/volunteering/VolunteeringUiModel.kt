package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

/**
 * *Devenir Hot'Staff* — what is asked, what is offered, and the two ways to act on it.
 *
 * [name] comes from the content rather than from a string, because the programme is branded and the
 * association renames it more readily than it renames a tab.
 *
 * There is no closed state. Recruiting is treated as always open for now — DECISIONS.md § Open has
 * the reasoning and the question it leaves behind.
 */
data class VolunteeringUiModel(
    val isLoading: Boolean,
    val name: String?,
    val body: String?,
    val perks: List<String>,
    val signupUrl: String?,
    val email: String?,
)
