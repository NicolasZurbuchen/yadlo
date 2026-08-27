package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.mapper

import io.nicolaszurbuchen.yadlo.core.content.domain.model.Phase
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.uimodel.PhaseUiModel

/**
 * Where the domain `Phase` becomes the one Accueil renders.
 *
 * **It is exhaustive by construction rather than by care.** A `when` over an enum with no `else`
 * stops compiling the day a sixth Phase is added, which is the whole reason the twin is worth
 * having: the alternative — the mapper reading the domain enum directly — would let a new Phase
 * reach the block stack as a silent no-op.
 *
 * It moved out of `HomeStoreFactory`, where it sat as a private function under 180 lines of store
 * wiring. Nothing about it was wrong there except that a file about building a Store was also the
 * only place this answer was written down.
 */
fun Phase.toUiModel(): PhaseUiModel =
    when (this) {
        Phase.OFF_SEASON -> PhaseUiModel.OFF_SEASON
        Phase.ANNOUNCED -> PhaseUiModel.ANNOUNCED
        Phase.APPROACHING -> PhaseUiModel.APPROACHING
        Phase.LIVE -> PhaseUiModel.LIVE
        Phase.ENDED -> PhaseUiModel.ENDED
    }
