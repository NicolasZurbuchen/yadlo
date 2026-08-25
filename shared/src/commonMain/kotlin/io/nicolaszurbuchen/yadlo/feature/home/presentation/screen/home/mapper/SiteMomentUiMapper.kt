package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.mapper

import io.nicolaszurbuchen.yadlo.feature.home.domain.model.SiteMoment
import io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.uimodel.SiteMomentUiModel

/**
 * Where the domain `SiteMoment` becomes the one the LIVE hero is drawn from.
 *
 * The instants travel across unchanged, and that is deliberate: *when* the site opens is a fact,
 * and `16:00` is a rendering of it in a particular zone. The rendering happens in `HomeUiMapper`
 * beside every other time on the screen, so this stays a shape conversion with no formatting in it.
 */
fun SiteMoment.toUiModel(): SiteMomentUiModel =
    when (this) {
        is SiteMoment.BeforeFirstDay -> SiteMomentUiModel.BeforeFirstDay(opensAt)
        is SiteMoment.Open -> SiteMomentUiModel.Open(closesAt)
        is SiteMoment.Closed -> SiteMomentUiModel.Closed(reopensAt)
        SiteMoment.Finished -> SiteMomentUiModel.Finished
    }
