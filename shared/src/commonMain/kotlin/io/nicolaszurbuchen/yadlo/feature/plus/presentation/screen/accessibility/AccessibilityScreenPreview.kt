package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors

/**
 * The empty one is first because it is the state that ships: the festival publishes nothing on
 * accessibility today, so this is the screen a wheelchair user actually meets. The filled one is
 * what it becomes the day the association answers.
 */
private class AccessibilityStateProvider : PreviewParameterProvider<AccessibilityUiModel> {
    override val values =
        sequenceOf(
            AccessibilityUiModel(
                isLoading = true,
                available = emptyList(),
                unavailable = emptyList(),
                contactEmail = null,
                nothingPublished = false,
                emptyMessage = null,
            ),
            nothingPublished(),
            answered(),
        )
}

@Preview
@Composable
private fun AccessibilityScreenPreview(
    @PreviewParameter(AccessibilityStateProvider::class) state: AccessibilityUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            AccessibilityScreen(state = state, onBackClick = {}, onContactClick = {})
        }
    }
}

private fun nothingPublished() =
    AccessibilityUiModel(
        isLoading = false,
        available = emptyList(),
        unavailable = emptyList(),
        contactEmail = "hello@yadlo.ch",
        nothingPublished = true,
        emptyMessage = null,
    )

private fun answered() =
    AccessibilityUiModel(
        isLoading = false,
        available =
            listOf(
                AccessibilityFactUiModel(id = "parking", name = "Places de parc réservées", note = "Deux, à l'entrée"),
                AccessibilityFactUiModel(id = "bus", name = "Bus 701 et 705 à plancher surbaissé", note = null),
            ),
        unavailable =
            listOf(
                AccessibilityFactUiModel(
                    id = "toilettes",
                    name = "Toilettes adaptées",
                    note = "Le site est une plage, sans raccordement",
                ),
            ),
        contactEmail = "hello@yadlo.ch",
        nothingPublished = false,
        emptyMessage = null,
    )
