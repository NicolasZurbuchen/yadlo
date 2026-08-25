package io.nicolaszurbuchen.yadlo.feature.home.presentation.screen.home

/**
 * The presentation twin of the domain `Phase`.
 *
 * It exists because the Store converts on the way out, so the domain enum stops at the Store
 * boundary the way `State` does. That is also what lets the UiMapper decide the block stack without
 * importing the domain layer, which the architecture forbids it.
 */
enum class PhaseUiModel {
    OFF_SEASON,
    ANNOUNCED,
    APPROACHING,
    LIVE,
    ENDED,
}
