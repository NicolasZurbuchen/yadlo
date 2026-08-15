package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.contact

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.contact_empty

fun ContactState.toUiModel(): ContactUiModel {
    if (!hasLoaded) {
        return ContactUiModel(
            isLoading = true,
            volunteering = null,
            emails = emptyList(),
            address = null,
            emptyMessage = null,
        )
    }

    val loaded =
        router ?: return ContactUiModel(
            isLoading = false,
            volunteering = null,
            emails = emptyList(),
            address = null,
            emptyMessage = UiText.Resource(Res.string.contact_empty),
        )

    return ContactUiModel(
        isLoading = false,
        volunteering =
            loaded.volunteering?.let {
                VolunteeringUiModel(
                    name = it.name,
                    body = it.body,
                    perks = it.perks,
                    signupUrl = it.signupUrl,
                    email = loaded.volunteeringEmail,
                )
            },
        // All nine, each under the label the association wrote for it. Choosing four would be
        // guessing at their internal division of labour, and a directory is what they published.
        emails = loaded.emails.map { ContactEmailUiModel(id = it.id, label = it.label, address = it.address) },
        // Joined into one block: it is a postal address and is read as one, never line by line.
        address = loaded.addressLines.joinToString("\n").ifEmpty { null },
        emptyMessage = null,
    )
}
