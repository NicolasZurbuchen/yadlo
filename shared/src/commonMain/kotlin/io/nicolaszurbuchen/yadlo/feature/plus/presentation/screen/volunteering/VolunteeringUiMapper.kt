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
        )

    return VolunteeringUiModel(
        isLoading = false,
        name = loaded.name,
        body = loaded.body,
        perks = loaded.perks,
        signupUrl = loaded.signupUrl,
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
