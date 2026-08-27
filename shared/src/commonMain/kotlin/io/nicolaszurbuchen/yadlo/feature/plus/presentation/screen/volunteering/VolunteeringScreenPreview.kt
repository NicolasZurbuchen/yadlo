package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.design.preview.YadloPreview
import io.nicolaszurbuchen.yadlo.feature.plus.presentation.uimodel.PlusEmailUiModel
import io.nicolaszurbuchen.yadlo.infra.preview.PreviewThemes

/**
 * The skeleton and the campaign. The closed state is gone with `hasLoaded` — recruiting is treated
 * as always open for now, and DECISIONS.md § Open holds the question that leaves behind.
 */
private class VolunteeringScreenStateProvider : PreviewParameterProvider<VolunteeringUiModel> {
    override val values =
        sequenceOf(
            VolunteeringUiModel(
                isLoading = true,
                name = null,
                body = null,
                perks = emptyList(),
                signupUrl = null,
                email = null,
                shareText = null,
            ),
            published(),
        )

    private fun published() =
        VolunteeringUiModel(
            isLoading = false,
            name = "Hot'Staff",
            body =
                "Les bénévoles s'engagent pour un minimum de 6 heures pendant l'événement, réparties en " +
                    "plusieurs postes — par exemple 3 × 2 heures. Les journées de montage et de démontage " +
                    "sont aussi les bienvenues.",
            perks =
                listOf(
                    "Tote bag et t-shirt Hot Staff",
                    "Boissons offertes au bar selon les heures effectuées",
                    "Repas végane chaque jour",
                ),
            signupUrl = "https://ehro.app/o/yadlo/",
            shareText = "Hot’Staff\nhttps://ehro.app/o/yadlo/",
            email =
                PlusEmailUiModel(
                    id = "staff",
                    label = "Staff",
                    responsible = "Maeva C.",
                    address = "staff@yadlo.ch",
                ),
        )
}

@PreviewThemes
@Composable
private fun VolunteeringScreenPreview(
    @PreviewParameter(VolunteeringScreenStateProvider::class) state: VolunteeringUiModel,
) {
    YadloPreview {
        VolunteeringScreen(state = state, onBackClick = {}, onSignupClick = {}, onEmailClick = {}, onShareClick = {})
    }
}
