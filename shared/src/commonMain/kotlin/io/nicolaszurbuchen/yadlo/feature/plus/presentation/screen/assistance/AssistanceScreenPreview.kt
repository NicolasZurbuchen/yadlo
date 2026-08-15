package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

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

private class AssistanceStateProvider : PreviewParameterProvider<AssistanceUiModel> {
    override val values =
        sequenceOf(
            AssistanceUiModel(isLoading = true, numbers = emptyList(), lostPropertyEmail = null, emptyMessage = null),
            published(),
        )
}

@Preview
@Composable
private fun AssistanceScreenPreview(
    @PreviewParameter(AssistanceStateProvider::class) state: AssistanceUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            AssistanceScreen(state = state, onBackClick = {}, onNumberClick = {}, onLostPropertyClick = {})
        }
    }
}

private fun published() =
    AssistanceUiModel(
        isLoading = false,
        numbers =
            listOf(
                EmergencyNumberUiModel(id = "urgences", number = "112", label = "Urgences (numéro européen)"),
                EmergencyNumberUiModel(id = "ambulance", number = "144", label = "Ambulance"),
                EmergencyNumberUiModel(id = "police", number = "117", label = "Police"),
                EmergencyNumberUiModel(id = "pompiers", number = "118", label = "Pompiers"),
            ),
        lostPropertyEmail = "hello@yadlo.ch",
        emptyMessage = null,
    )
