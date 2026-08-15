package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.payment

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.payment_empty

fun PaymentState.toUiModel(): PaymentUiModel {
    if (!hasLoaded) {
        return PaymentUiModel(
            isLoading = true,
            accepted = emptyList(),
            refused = emptyList(),
            notes = emptyList(),
            links = emptyList(),
            emptyMessage = null,
        )
    }

    val loaded =
        payment ?: return PaymentUiModel(
            isLoading = false,
            accepted = emptyList(),
            refused = emptyList(),
            notes = emptyList(),
            links = emptyList(),
            emptyMessage = UiText.Resource(Res.string.payment_empty),
        )

    return PaymentUiModel(
        isLoading = false,
        // Names as the content writes them — "Cartes Visa, Mastercard et Maestro" is the
        // association's own phrasing and shortening it would be deciding which cards matter.
        accepted = loaded.methods.filter { it.accepted }.map { it.name },
        refused = loaded.methods.filterNot { it.accepted }.map { it.name },
        notes = loaded.notes.map { it.body },
        links =
            loaded.links.map {
                PaymentLinkUiModel(id = it.id, label = it.label, sublabel = it.sublabel, url = it.url)
            },
        emptyMessage = null,
    )
}
