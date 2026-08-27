package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

private class AssistanceScreenStateProvider : PreviewParameterProvider<AssistanceUiModel> {
    override val values =
        sequenceOf(
            AssistanceUiModel(
                isLoading = true,
                numbers = emptyList(),
                recognition = emptyList(),
                lostPropertyEmail = null,
                emptyMessage = null,
            ),
            published(),
        )

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
            recognition = listOf("T-shirts Hot’Staff — il y en a 160 sur le site"),
            lostPropertyEmail = "hello@yadlo.ch",
            emptyMessage = null,
        )
}

@PreviewThemes
@Composable
private fun AssistanceScreenPreview(
    @PreviewParameter(AssistanceScreenStateProvider::class) state: AssistanceUiModel,
) {
    YadloPreview {
        AssistanceScreen(state = state, onBackClick = {}, onNumberClick = {}, onLostPropertyClick = {})
    }
}
