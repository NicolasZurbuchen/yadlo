package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel.PlusEmailUiModel

fun VolunteeringState.toUiModel(): VolunteeringUiModel {
    val loaded =
        offer ?: return VolunteeringUiModel(
            isLoading = true,
            name = null,
            body = null,
            perks = emptyList(),
            signupUrl = null,
            email = null,
            shareText = null,
        )

    return VolunteeringUiModel(
        isLoading = false,
        name = loaded.name,
        body = loaded.body,
        perks = loaded.perks,
        signupUrl = loaded.signupUrl,
        // Name, one line of the ask, and the address that receives it. Built from the content
        // rather than written as a string so it renames itself when the programme does — the
        // association calls this Hot’Staff today and has renamed it before.
        shareText =
            loaded.signupUrl?.let { url ->
                listOfNotNull(loaded.name, loaded.body, url).joinToString("\n")
            },
        email =
            loaded.email?.let {
                PlusEmailUiModel(
                    id = it.id,
                    label = it.label,
                    responsible = it.responsible,
                    address = it.address,
                )
            },
    )
}
