package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.assistance

import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.assistance_empty

fun AssistanceState.toUiModel(): AssistanceUiModel {
    if (!hasLoaded) {
        return AssistanceUiModel(
            isLoading = true,
            numbers = emptyList(),
            recognition = emptyList(),
            lostPropertyEmail = null,
            emptyMessage = null,
        )
    }

    val loaded =
        guide ?: return AssistanceUiModel(
            isLoading = false,
            numbers = emptyList(),
            recognition = emptyList(),
            lostPropertyEmail = null,
            emptyMessage = UiText.Resource(Res.string.assistance_empty),
        )

    return AssistanceUiModel(
        isLoading = false,
        // The content's order, which puts the European 112 ahead of the Swiss three. That is the
        // association's choice about its own visitors, not one to re-sort here.
        numbers =
            loaded.numbers.map {
                EmergencyNumberUiModel(id = it.id, number = it.number, label = it.label)
            },
        recognition = loaded.recognition.map { it.text },
        lostPropertyEmail = loaded.lostPropertyEmail,
        emptyMessage =
            if (loaded.numbers.isEmpty() && loaded.lostPropertyEmail == null) {
                UiText.Resource(Res.string.assistance_empty)
            } else {
                null
            },
    )
}
