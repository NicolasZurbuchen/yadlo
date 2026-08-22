package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel.PlusEmailUiModel

/**
 * *Devenir Hot'Staff* — what is asked, what is offered, and the two ways to act on it.
 *
 * [name] comes from the content rather than from a string, because the programme is branded and the
 * association renames it more readily than it renames a tab.
 *
 * [email] carries the directory entry — the concern, whoever is behind it, and the address — so the
 * tile reads as the ones on *Nous écrire* rather than as a bare `staff@yadlo.ch` somebody pasted in.
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
    val email: PlusEmailUiModel?,
    /**
     * The message the share sheet sends, and null when there is no [signupUrl] to put in it.
     *
     * **It carries the association’s own recruitment address, never this app.** Somebody
     * forwarding this is doing the association a favour, and what they send has to work for a
     * recipient who has never heard of this app — which is every recipient.
     */
    val shareText: String?,
)
