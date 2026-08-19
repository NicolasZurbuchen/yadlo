package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel.PlusEmailUiModel

fun ContactState.toUiModel(): ContactUiModel {
    val loaded =
        router ?: return ContactUiModel(
            isLoading = true,
            emails = emptyList(),
            address = null,
        )

    return ContactUiModel(
        isLoading = false,
        // All nine, each under the label the association wrote for it. Choosing four would be
        // guessing at their internal division of labour, and a directory is what they published.
        emails =
            loaded.emails.map {
                PlusEmailUiModel(
                    id = it.id,
                    label = it.label,
                    responsible = it.responsible,
                    address = it.address,
                )
            },
        // Joined into one block: it is a postal address and is read as one, never line by line.
        address = loaded.addressLines.joinToString("\n").ifEmpty { null },
    )
}
