package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.volunteering_empty

fun VolunteeringState.toUiModel(): VolunteeringUiModel {
    if (!hasLoaded) {
        return VolunteeringUiModel(
            isLoading = true,
            name = null,
            body = null,
            perks = emptyList(),
            signupUrl = null,
            email = null,
            emptyMessage = null,
        )
    }

    val loaded =
        offer ?: return VolunteeringUiModel(
            isLoading = false,
            name = null,
            body = null,
            perks = emptyList(),
            signupUrl = null,
            email = null,
            emptyMessage = UiText.Resource(Res.string.volunteering_empty),
        )

    return VolunteeringUiModel(
        isLoading = false,
        name = loaded.name,
        body = loaded.body,
        perks = loaded.perks,
        signupUrl = loaded.signupUrl,
        email = loaded.email,
        emptyMessage = null,
    )
}
