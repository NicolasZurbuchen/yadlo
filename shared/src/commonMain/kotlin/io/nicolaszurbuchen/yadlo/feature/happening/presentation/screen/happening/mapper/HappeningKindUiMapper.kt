package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.mapper

import io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningKind
import io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.HappeningKindUiModel

/**
 * The fiche's own converter, called from the UiMapper rather than from the Store.
 *
 * It used to run at the Store boundary, so that the State could carry the presentation twin and the
 * UiMapper would never have to name a domain type. That arrangement made the State hold the kind
 * twice — once inside [io.nicolaszurbuchen.yadlo.feature.happening.domain.model.HappeningDetail]
 * and once beside it — which is two sources for one fact and a frame where they could disagree.
 * The detail is the source now, and this is where it is read.
 */
fun HappeningKind.toUiModel(): HappeningKindUiModel =
    when (this) {
        HappeningKind.ARTIST -> HappeningKindUiModel.ARTIST
        HappeningKind.ACTIVITY -> HappeningKindUiModel.ACTIVITY
        HappeningKind.STAND -> HappeningKindUiModel.STAND
    }
