package io.nicolaszurbuchen.yadlo.feature.plus.presentation.screen.volunteering

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import io.nicolaszurbuchen.yadlo.app.design.theme.YadloTheme
import io.nicolaszurbuchen.yadlo.app.design.theme.appColors
import io.nicolaszurbuchen.yadlo.infra.ui.UiText
import yadlo.shared.generated.resources.Res
import yadlo.shared.generated.resources.volunteering_empty

/** Recruiting open, and recruiting closed — the second is a real state every year in August. */
private class VolunteeringStateProvider : PreviewParameterProvider<VolunteeringUiModel> {
    override val values =
        sequenceOf(
            VolunteeringUiModel(
                isLoading = true,
                name = null,
                body = null,
                perks = emptyList(),
                signupUrl = null,
                email = null,
                emptyMessage = null,
            ),
            published(),
            closed(),
        )
}

@Preview
@Composable
private fun VolunteeringScreenPreview(
    @PreviewParameter(VolunteeringStateProvider::class) state: VolunteeringUiModel,
) {
    YadloTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.appColors.background)) {
            VolunteeringScreen(state = state, onBackClick = {}, onSignupClick = {}, onEmailClick = {})
        }
    }
}

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
        email = "staff@yadlo.ch",
        emptyMessage = null,
    )

private fun closed() =
    VolunteeringUiModel(
        isLoading = false,
        name = null,
        body = null,
        perks = emptyList(),
        signupUrl = null,
        email = null,
        emptyMessage = UiText.Resource(Res.string.volunteering_empty),
    )
