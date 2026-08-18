package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.story

import io.nicolaszurbuchen.yadlo.app.design.uimodel.FigureUiModel
import io.nicolaszurbuchen.yadlo.infra.ui.UiText

/**
 * *L'histoire de Yadlo* — the origin, a day there, and the last edition in three numbers.
 *
 * [figuresCaveat] is Provenance earning its keep: the association has published closing figures
 * once, and a screen that showed 2024's numbers under a 2026 heading without saying so would be
 * the most quietly wrong thing in the app.
 */
data class StoryUiModel(
    val isLoading: Boolean,
    val body: String?,
    val passageTitle: String?,
    val passageBody: String?,
    val figures: List<FigureUiModel>,
    val figuresCaveat: UiText?,
)
