package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.about

/**
 * *À propos de cette app* — the one screen whose words are all app strings, and the one fact about
 * it that is not.
 *
 * A UiModel for a single field rather than a parameter on the screen, because a screen takes its
 * Modifier, its callbacks and its model and nothing else. [version] comes from the binary through
 * `BuildFlags` and so has to travel the same road as anything else the screen did not author, even
 * though there is no store behind it and there is not going to be one.
 */
data class AboutUiModel(
    val version: String,
)
