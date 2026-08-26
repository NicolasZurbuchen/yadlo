package io.nicolaszurbuchen.yadlo.feature.happening.presentation.screen.happening.uimodel

/**
 * The presentation twin of the domain `HappeningKind`, for the same reason `PhaseUiModel` is one:
 * which of the three a Happening is decides one sentence, and picking a sentence is not a domain
 * decision. `mapper/HappeningKindUiMapper.kt` converts, off the detail the State already holds.
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
