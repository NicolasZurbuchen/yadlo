package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home.uimodel

import kotlin.time.Instant

/**
 * The presentation twin of the domain `SiteMoment`, for the same reason [PhaseUiModel] is one: the
 * Store converts on the way out, so the domain type stops at the Store boundary and the UiMapper
 * can decide what Accueil says without importing a layer the architecture forbids it.
 *
 * The instants travel as [Instant] rather than as formatted strings because *when* the site opens is
 * a fact and `16:00` is a rendering of it — the zone it is written in belongs to the mapper, which
 * is where every other time on this screen is formatted.
 */
sealed interface SiteMomentUiModel {
    data class BeforeFirstDay(
        val opensAt: Instant,
    ) : SiteMomentUiModel

    data class Open(
        val closesAt: Instant,
    ) : SiteMomentUiModel

    data class Closed(
        val reopensAt: Instant,
    ) : SiteMomentUiModel

    data object Finished : SiteMomentUiModel
}
