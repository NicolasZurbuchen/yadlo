package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.accessibility

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.accessibility_empty

fun AccessibilityState.toUiModel(): AccessibilityUiModel {
    if (!hasLoaded) {
        return AccessibilityUiModel(
            isLoading = true,
            available = emptyList(),
            unavailable = emptyList(),
            contactEmail = null,
            nothingPublished = false,
            emptyMessage = null,
        )
    }

    val loaded =
        guide ?: return AccessibilityUiModel(
            isLoading = false,
            available = emptyList(),
            unavailable = emptyList(),
            contactEmail = null,
            nothingPublished = false,
            emptyMessage = UiText.Resource(Res.string.accessibility_empty),
        )

    return AccessibilityUiModel(
        isLoading = false,
        available =
            loaded.available.map { AccessibilityFactUiModel(id = it.id, name = it.name, note = it.note) },
        unavailable =
            loaded.unavailable.map { AccessibilityFactUiModel(id = it.id, name = it.name, note = it.note) },
        contactEmail = loaded.contactEmail,
        // The section is published and holds nothing, which is the state today. Distinct from a
        // missing section: one says "we have not answered this yet", the other is a content bug.
        nothingPublished = loaded.available.isEmpty() && loaded.unavailable.isEmpty(),
        emptyMessage = null,
    )
}
