package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import io.nicolaszurbuchen.yadlo.infra.text.UiText

/**
 * *En cas de besoin* — one screen for one situation, not a drawer of three short subjects.
 *
 * The merge is justified because everything here answers "something has gone wrong", which is a
 * different mode from browsing practical information. That three separate entries would each have
 * been short is not the reason and would not have been one.
 */
data class AssistanceUiModel(
    val isLoading: Boolean,
    val numbers: List<EmergencyNumberUiModel>,
    /** How to tell who works here. Empty until the content says, and the section goes with it. */
    val recognition: List<String>,
    val lostPropertyEmail: String?,
    val emptyMessage: UiText?,
)

/**
 * [number] as published — spaced, grouped, whatever the association wrote. What gets dialled is
 * stripped of everything that is not a digit, so the two never have to be kept in step.
 */
data class EmergencyNumberUiModel(
    val id: String,
    val number: String,
    val label: String,
)
