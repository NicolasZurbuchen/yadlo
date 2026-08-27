package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.faq

import io.nicolaszurbuchen.yadlo.infra.text.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.faq_empty

fun FaqState.toUiModel(): FaqUiModel {
    val loaded =
        entries ?: return FaqUiModel(isLoading = true, entries = emptyList(), emptyMessage = null)

    return FaqUiModel(
        isLoading = false,
        entries = loaded.map { FaqEntryUiModel(id = it.id, question = it.question, answer = it.answer) },
        emptyMessage = if (loaded.isEmpty()) UiText.Resource(Res.string.faq_empty) else null,
    )
}
