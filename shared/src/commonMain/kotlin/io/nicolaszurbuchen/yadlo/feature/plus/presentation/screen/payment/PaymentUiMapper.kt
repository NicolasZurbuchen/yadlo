package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

fun PaymentState.toUiModel(): PaymentUiModel {
    val loaded =
        payment ?: return PaymentUiModel(
            isLoading = true,
            headline = null,
            summary = null,
            methods = emptyList(),
            notes = emptyList(),
        )

    return PaymentUiModel(
        isLoading = false,
        headline = loaded.headline,
        summary = loaded.summary,
        // Accepted first, refused after, in one list. Names as the content writes them — "Visa,
        // Mastercard et Maestro — sans contact" is the association's own phrasing and shortening it
        // would be deciding which cards matter.
        methods =
            loaded.methods
                .sortedByDescending { it.accepted }
                .map { PaymentMethodUiModel(id = it.id, name = it.name, accepted = it.accepted) },
        notes =
            loaded.notes.map { note ->
                PaymentNoteUiModel(
                    id = note.id,
                    title = note.title,
                    body = note.body,
                    links =
                        note.links.map {
                            PaymentLinkUiModel(id = it.id, label = it.label, sublabel = it.sublabel, url = it.url)
                        },
                )
            },
    )
}
