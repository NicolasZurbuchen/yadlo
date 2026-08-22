package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening

/**
 * The presentation twin of the domain `HappeningKind`, for the same reason `PhaseUiModel` is one:
 * the Store converts on the way out, so the domain enum stops at the Store boundary and the UiMapper
 * can pick a sentence without importing a layer the architecture forbids it.
 *
 * Only the share message reads it. Every section the fiche draws is still decided by whether the
 * content behind it exists — a price, a menu, a heart — which is what lets one screen serve all
 * three kinds without a branch per section.
 */
enum class HappeningKindUiModel {
    ARTIST,
    ACTIVITY,
    STAND,
}
